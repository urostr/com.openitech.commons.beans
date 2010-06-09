/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db.model;

import java.util.List;
import javax.swing.RowSorter.SortKey;

/**
 *
 * @author uros
 */
public interface DbSortable {

    /**
     * Reverses the sort order of the specified column.  It is up to
     * subclasses to provide the exact behavior when invoked.  Typically
     * this will reverse the sort order from ascending to descending (or
     * descending to ascending) if the specified column is already the
     * primary sorted column; otherwise, makes the specified column
     * the primary sorted column, with an ascending sort order.  If
     * the specified column is not sortable, this method has no
     * effect.
     * <p>
     * If this results in changing the sort order and sorting, the
     * appropriate <code>RowSorterListener</code> notification will be
     * sent.
     *
     * @param column the column to toggle the sort ordering of, in
     *        terms of the underlying model
     * @throws IndexOutOfBoundsException if column is outside the range of
     *         the underlying model
     */
    public abstract void toggleSortOrder(int columnIndex, String column);


    /**
     * Sets the current sort keys.
     *
     * @param keys the new <code>SortKeys</code>; <code>null</code>
     *        is a shorthand for specifying an empty list,
     *        indicating that the view should be unsorted
     */
    public abstract void setSortKeys(List<? extends SortKey> keys);

    /**
     * Returns the current sort keys.  This must return a {@code
     * non-null List} and may return an unmodifiable {@code List}. If
     * you need to change the sort keys, make a copy of the returned
     * {@code List}, mutate the copy and invoke {@code setSortKeys}
     * with the new list.
     *
     * @return the current sort order
     */
    public abstract List<? extends SortKey> getSortKeys();

}
