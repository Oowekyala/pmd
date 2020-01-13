/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.database;


import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.SourceCode.ReaderCodeLoader;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.ReaderDataSource;

public final class DbUtil {

    private static final Logger LOG = Logger.getLogger(DbUtil.class.getName());

    private DbUtil() {

    }

    /**
     * Parses the given string as a database uri and returns a list of
     * datasources.
     *
     * @param uriString the URI to parse
     *
     * @return list of data sources
     *
     * @throws PMDException if the URI couldn't be parsed
     * @see DBURI
     */
    public static List<DataSource> getURIDataSources(String uriString) throws PMDException {
        List<DataSource> dataSources = new ArrayList<>();

        try {
            DBURI dbUri = new DBURI(uriString);
            DBMSMetadata dbmsMetadata = new DBMSMetadata(dbUri);
            LOG.log(Level.FINE, "DBMSMetadata retrieved");
            List<SourceObject> sourceObjectList = dbmsMetadata.getSourceObjectList();
            LOG.log(Level.FINE, "Located {0} database source objects", sourceObjectList.size());
            for (SourceObject sourceObject : sourceObjectList) {
                String falseFilePath = sourceObject.getPseudoFileName();
                LOG.log(Level.FINEST, "Adding database source object {0}", falseFilePath);

                try {
                    dataSources.add(new ReaderDataSource(dbmsMetadata.getSourceCode(sourceObject), falseFilePath));
                } catch (SQLException ex) {
                    if (LOG.isLoggable(Level.WARNING)) {
                        LOG.log(Level.WARNING, "Cannot get SourceCode for " + falseFilePath + "  - skipping ...", ex);
                    }
                }
            }
        } catch (URISyntaxException e) {
            throw new PMDException("Cannot get DataSources from DBURI - \"" + uriString + "\"", e);
        } catch (SQLException e) {
            throw new PMDException(
                "Cannot get DataSources from DBURI, couldn't access the database - \"" + uriString + "\"", e);
        } catch (ClassNotFoundException e) {
            throw new PMDException(
                "Cannot get DataSources from DBURI, probably missing database jdbc driver - \"" + uriString + "\"",
                e);
        } catch (Exception e) {
            throw new PMDException("Encountered unexpected problem with URI \"" + uriString + "\"", e);
        }
        return dataSources;
    }

    public static List<SourceCode> getDbUriSourceCodeList(String uri) {
        try {
            LOG.fine(String.format("Attempting DBURI=%s", uri));
            DBURI dburi = new DBURI(uri);
            LOG.fine(String.format("Initialised DBURI=%s", dburi));
            LOG.fine(
                String.format("Adding DBURI=%s with DBType=%s", dburi.toString(), dburi.getDbType().toString()));
            return getUriSourceCode(dburi);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("uri=" + uri, e);
        }
    }

    public static List<SourceCode> getUriSourceCode(DBURI dburi) throws IOException {
        List<SourceCode> result = new ArrayList<>();
        try {
            DBMSMetadata dbmsmetadata = new DBMSMetadata(dburi);

            List<SourceObject> sourceObjectList = dbmsmetadata.getSourceObjectList();
            LOG.log(Level.FINER, "Located {0} database source objects", sourceObjectList.size());

            for (SourceObject sourceObject : sourceObjectList) {
                // Add DBURI as a faux-file
                String falseFilePath = sourceObject.getPseudoFileName();
                LOG.log(Level.FINEST, "Adding database source object {0}", falseFilePath);

                result.add(new SourceCode(new ReaderCodeLoader(dbmsmetadata.getSourceCode(sourceObject), falseFilePath)));
            }
        } catch (Exception sqlException) {
            LOG.log(Level.SEVERE, "Problem with Input URI", sqlException);
            throw new RuntimeException("Problem with DBURI: " + dburi, sqlException);
        }
        return result;
    }
}
