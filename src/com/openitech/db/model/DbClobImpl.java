/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

/**
 *
 * @author domenbasic
 */
public class DbClobImpl implements java.sql.Clob {

    private Reader reader;
    private int length;
    private boolean needsReset = false;

    public DbClobImpl(Reader reader, int length) {
        this.reader = reader;
        this.length = length;
    }

    public DbClobImpl(String clob) {
        reader = new StringReader(clob);
        length = clob.length();
    }

    @Override
    public long length() throws SQLException {
        return length;
    }

    @Override
    public String getSubString(long pos, int length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Reader getCharacterStream() throws SQLException {
        try {
            if (needsReset) {
                reader.reset();
            }
        } catch (Exception ioe) {
            throw new SQLException("could not reset reader");
        }
        needsReset = true;
        return reader;

    }

    @Override
    public InputStream getAsciiStream() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");

    }

    @Override
    public long position(String searchstr, long start) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long position(Clob searchstr, long start) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int setString(long pos, String str) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int setString(long pos, String str, int offset, int len) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OutputStream setAsciiStream(long pos) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Writer setCharacterStream(long pos) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void truncate(long len) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void free() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Reader getCharacterStream(long pos, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
