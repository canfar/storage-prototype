/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2017.                            (c) 2017.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits réservés
 *
 *  NRC disclaims any warranties,        Le CNRC dénie toute garantie
 *  expressed, implied, or               énoncée, implicite ou légale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           être tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou général,
 *  arising from the use of the          accessoire ou fortuit, résultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        être utilisés pour approuver ou
 *  products derived from this           promouvoir les produits dérivés
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  préalable et particulière
 *                                       par écrit.
 *
 *  This file is part of the             Ce fichier fait partie du projet
 *  OpenCADC project.                    OpenCADC.
 *
 *  OpenCADC is free software:           OpenCADC est un logiciel libre ;
 *  you can redistribute it and/or       vous pouvez le redistribuer ou le
 *  modify it under the terms of         modifier suivant les termes de
 *  the GNU Affero General Public        la “GNU Affero General Public
 *  License as published by the          License” telle que publiée
 *  Free Software Foundation,            par la Free Software Foundation
 *  either version 3 of the              : soit la version 3 de cette
 *  License, or (at your option)         licence, soit (à votre gré)
 *  any later version.                   toute version ultérieure.
 *
 *  OpenCADC is distributed in the       OpenCADC est distribué
 *  hope that it will be useful,         dans l’espoir qu’il vous
 *  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
 *  without even the implied             GARANTIE : sans même la garantie
 *  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
 *  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
 *  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
 *  General Public License for           Générale Publique GNU Affero
 *  more details.                        pour plus de détails.
 *
 *  You should have received             Vous devriez avoir reçu une
 *  a copy of the GNU Affero             copie de la Licence Générale
 *  General Public License along         Publique GNU Affero avec
 *  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
 *  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
 *                                       <http://www.gnu.org/licenses/>.
 *
 *
 ************************************************************************
 */

package org.opencadc.storage;


import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.accumulators.Accumulator;
import org.apache.flink.api.common.accumulators.SimpleAccumulator;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileStatus;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.OutputFormatSinkFunction;
import org.apache.flink.util.Collector;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;


public class SearcherApp
{
    public static void main(final String[] args) throws Exception
    {
        final ParameterTool params = ParameterTool.fromArgs(args);
        final StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();

        final Path path = new Path(params.get("root"));
        final String userTerm = params.get("term");
        final int parallelism = params.getInt("parallelism", 1);

        final DataStream<FileStatus> inputStream =
                executionEnvironment.fromCollection(Arrays.asList(path.getFileSystem().listStatus(path)));

        inputStream.flatMap(new FlatMapFunction<FileStatus, FileStatus>()
        {
            @Override
            public void flatMap(FileStatus value, Collector<FileStatus> out) throws Exception
            {

                if (value.isDir())
                {
                    final RecursiveFilePathLookup pathLookup = new RecursiveFilePathLookup(value.getPath());
                    pathLookup.gatherPaths(out);
                }
                else
                {
                    out.collect(value);
                }
            }
        }).filter(new FilterFunction<FileStatus>()
        {
            /**
             * Rules to determine which entries are KEPT, NOT filtered out.
             * @param value     The next value to check.
             * @return True if it is to be kept, False otherwise.
             * @throws Exception    Any thing goes awry.
             */
            @Override
            public boolean filter(FileStatus value) throws Exception
            {
                return value.getPath().getPath().matches(".*" + userTerm + ".*");
            }
        }).flatMap(new RichFlatMapFunction<FileStatus, String>()
        {
            private final Accumulator<String, ArrayList<String>> accumulator = new SearchResultsAccumulator();

            @Override
            public void open(Configuration parameters) throws Exception
            {
                getRuntimeContext().addAccumulator("output", accumulator);
            }

            @Override
            public void flatMap(FileStatus value, Collector<String> out) throws Exception
            {
                accumulator.add(value.getPath().getPath());
            }
        });

        final JobExecutionResult result =
                executionEnvironment.setParallelism(parallelism).execute(String.format("Search Metadata (%d)",
                                                                                       parallelism));
        final ArrayList<String> accumulatedResults = result.getAccumulatorResult("output");
        System.out.println("********************");
        System.out.println("Results: "
                           + Arrays.toString(accumulatedResults.toArray(new String[accumulatedResults.size()])));
        System.out.println("********************");
    }

    static class RecursiveFilePathLookup
    {
        final Path root;

        RecursiveFilePathLookup(Path root)
        {
            this.root = root;
        }


        void gatherPaths(Collector<FileStatus> paths) throws IOException
        {
            final FileStatus[] statii = root.getFileSystem().listStatus(root);

            for (final FileStatus fileStatus : statii)
            {
                if (fileStatus.isDir())
                {
                    final RecursiveFilePathLookup subDirLookup = new RecursiveFilePathLookup(fileStatus.getPath());
                    subDirLookup.gatherPaths(paths);
                }
                else
                {
                    paths.collect(fileStatus);
                }
            }
        }
    }
}
