package org.codehaus.mojo.javascript.compress;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.codehaus.plexus.util.IOUtil;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * A JS compressor that uses Dojo modified Rhino engine to compress the script.
 * The resulting compressed-js is garanteed to be functionaly equivalent as this
 * is the internal view of the rhino context.
 * 
 * @author <a href="mailto:nicolas@apache.org">nicolas De Loof</a>
 */
public class YahooUICompressor
    implements JSCompressor
{
    ErrorReporter4Mojo log = null;

    /**
     * Set a the JSCompressorLogger implementation that will receive logs
     *
     * @param logger a logger
     */
    public void setLogger(JSCompressorLogger logger) throws CompressionException
    {
        log = new ErrorReporter4Mojo(logger,true);
    }

    /**
     * Return current JSCompressorLogger used for logging
     *
     * @return the current JSCompressorLogger used for logging
     */
    public JSCompressorLogger getLogger() throws CompressionException
    {
        return log.getLogger();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.mojo.javascript.compress.JSCompressor#compress(java.io.File,
     * java.io.File, int, int)
     */
    public void compress( final File input, File compressed, int level, int language)
        throws CompressionException
    {
        FileWriter out = null;
        try
        {
            JavaScriptCompressor compressor =
                new JavaScriptCompressor( new FileReader( input ), log );

            int linebreakpos = (level < 6) ? 0 : (level < 9) ? 120 : -1;
            boolean nomunge = level < 3;
            level = (level>2) ? ((level>5) ? level-6 : level-3) : level;
            boolean preserveAllSemiColons = level < 2;
            boolean disableOptimizations = level < 1;

            out = new FileWriter( compressed );
            compressor.compress( out, linebreakpos, !nomunge, true, preserveAllSemiColons, disableOptimizations );
        }
        catch ( Exception e )
        {
            throw new CompressionException( "Failed to create compressed file", e, input );
        }
        finally
        {
            IOUtil.close( out );
        }
    }
}
