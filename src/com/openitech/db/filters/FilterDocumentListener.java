package com.openitech.db.filters;

import com.openitech.db.filters.Scheduler.SeekValueUpdateRunnable;
import com.openitech.formats.FormatFactory;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class FilterDocumentListener extends Scheduler implements DocumentListener {

    protected DataSourceFilters filter;
    protected DataSourceFilters.AbstractSeekType<? extends Object> seek_type;

    public FilterDocumentListener(DataSourceFilters filter, DataSourceFilters.AbstractSeekType<? extends Object> seek_type) {
        this(filter, seek_type, Scheduler.DELAY);
    }

    public FilterDocumentListener(DataSourceFilters filter, DataSourceFilters.AbstractSeekType<? extends Object> seek_type, long delay) {
        this.filter = filter;
        this.seek_type = seek_type;
        this.delay = delay;
    }

    public String getText(DocumentEvent e) {
        return getText(e.getDocument());
    }

    public String getText(Document doc) {
        String txt;
        try {
            txt = doc.getText(0, doc.getLength());
        } catch (BadLocationException err) {
            txt = null;
        }
        return txt;
    }

    public void changedUpdate(DocumentEvent e) {
        setSeekValue(e);
    }

    public void insertUpdate(DocumentEvent e) {
        setSeekValue(e);
    }

    public void removeUpdate(DocumentEvent e) {
        setSeekValue(e);
    }

    protected void setSeekValue(final DocumentEvent e) {
        if (seek_type instanceof DataSourceFilters.DateSeekType) {
            try {
                java.util.Date value = FormatFactory.DATE_FORMAT.parse(getText(e));
                schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, value));
            } catch (ParseException ex) {
                Logger.getLogger(FilterDocumentListener.class.getName()).log(Level.WARNING, ex.getMessage());
                schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, null));
            }
        } else {
            schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, getText(e)));
        }
    }
}