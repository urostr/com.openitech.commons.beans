/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.io;

import java.util.Formatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class LogWriter {

  private final StringBuffer sb = new StringBuffer(4096);
  private final Logger logger;
  private final Level level;
  /**
   * Line separator string.  This is the value of the line.separator
   * property at the moment that the writer was created.
   */
  private final String lineSeparator;
  private Formatter formatter;

  /**
   * Track both the text- and character-output streams, so that their buffers
   * can be flushed without flushing the entire stream.
   */
  public LogWriter(Logger logger, Level level) {
    this.logger = logger;
    this.level = level;
    this.lineSeparator = System.getProperty("line.separator", "\n");
  }

  public void flush() {
    flush(logger, level, null);
  }

  public void flush(Throwable thrown) {
    flush(logger, level, thrown);
  }

  public void flush(Logger logger, Level level, Throwable thrown) {
    final LogRecord log = new LogRecord(level, sb.toString());
    if (!log.getMessage().isEmpty()) {
      inferCaller(log);
      log.setThrown(thrown);
      logger.log(log);
      sb.setLength(0);
    }
  }

  public Logger getLogger() {
    return logger;
  }

// Private method to infer the caller's class and method names
  private void inferCaller(LogRecord log) {
    // Get the stack trace.
    StackTraceElement stack[] = (new Throwable()).getStackTrace();
    // First, search back to a method in the Logger class.
    int ix = 0;
    while (ix < stack.length) {
      StackTraceElement frame = stack[ix];
      String cname = frame.getClassName();
      if (cname.equals("com.openitech.io.LogWriter")) {
        break;
      }
      ix++;
    }
    // Now search for the first frame before the "Logger" class.
    while (ix < stack.length) {
      StackTraceElement frame = stack[ix];
      String cname = frame.getClassName();
      if (!cname.equals("com.openitech.io.LogWriter")) {
        // We've found the relevant frame.
        log.setSourceClassName(cname);
        log.setSourceMethodName(frame.getMethodName());
        return;
      }
      ix++;
    }
    // We haven't found a suitable frame, so just punt.  This is
    // OK as we are only committed to making a "best effort" here.
  }
  /* Methods that do not terminate lines */

  /**
   * Prints a boolean value.  The string produced by <code>{@link
   * java.lang.String#valueOf(boolean)}</code> is translated into bytes
   * according to the platform's default character encoding, and these bytes
   * are written in exactly the manner of the
   * <code>{@link #write(int)}</code> method.
   *
   * @param      b   The <code>boolean</code> to be printed
   */
  public void print(boolean b) {
    write(b ? "true" : "false");
  }

  /**
   * Prints a character.  The character is translated into one or more bytes
   * according to the platform's default character encoding, and these bytes
   * are written in exactly the manner of the
   * <code>{@link #write(int)}</code> method.
   *
   * @param      c   The <code>char</code> to be printed
   */
  public void print(char c) {
    write(String.valueOf(c));
  }

  /**
   * Prints an integer.  The string produced by <code>{@link
   * java.lang.String#valueOf(int)}</code> is translated into bytes
   * according to the platform's default character encoding, and these bytes
   * are written in exactly the manner of the
   * <code>{@link #write(int)}</code> method.
   *
   * @param      i   The <code>int</code> to be printed
   * @see        java.lang.Integer#toString(int)
   */
  public void print(int i) {
    write(String.valueOf(i));
  }

  /**
   * Prints a long integer.  The string produced by <code>{@link
   * java.lang.String#valueOf(long)}</code> is translated into bytes
   * according to the platform's default character encoding, and these bytes
   * are written in exactly the manner of the
   * <code>{@link #write(int)}</code> method.
   *
   * @param      l   The <code>long</code> to be printed
   * @see        java.lang.Long#toString(long)
   */
  public void print(long l) {
    write(String.valueOf(l));
  }

  /**
   * Prints a floating-point number.  The string produced by <code>{@link
   * java.lang.String#valueOf(float)}</code> is translated into bytes
   * according to the platform's default character encoding, and these bytes
   * are written in exactly the manner of the
   * <code>{@link #write(int)}</code> method.
   *
   * @param      f   The <code>float</code> to be printed
   * @see        java.lang.Float#toString(float)
   */
  public void print(float f) {
    write(String.valueOf(f));
  }

  /**
   * Prints a double-precision floating-point number.  The string produced by
   * <code>{@link java.lang.String#valueOf(double)}</code> is translated into
   * bytes according to the platform's default character encoding, and these
   * bytes are written in exactly the manner of the <code>{@link
   * #write(int)}</code> method.
   *
   * @param      d   The <code>double</code> to be printed
   * @see        java.lang.Double#toString(double)
   */
  public void print(double d) {
    write(String.valueOf(d));
  }

  /**
   * Prints an array of characters.  The characters are converted into bytes
   * according to the platform's default character encoding, and these bytes
   * are written in exactly the manner of the
   * <code>{@link #write(int)}</code> method.
   *
   * @param      s   The array of chars to be printed
   *
   * @throws  NullPointerException  If <code>s</code> is <code>null</code>
   */
  public void print(char s[]) {
    write(s);
  }

  /**
   * Prints a string.  If the argument is <code>null</code> then the string
   * <code>"null"</code> is printed.  Otherwise, the string's characters are
   * converted into bytes according to the platform's default character
   * encoding, and these bytes are written in exactly the manner of the
   * <code>{@link #write(int)}</code> method.
   *
   * @param      s   The <code>String</code> to be printed
   */
  public void print(String s) {
    if (s == null) {
      s = "null";
    }
    write(s);
  }

  /**
   * Prints an object.  The string produced by the <code>{@link
   * java.lang.String#valueOf(Object)}</code> method is translated into bytes
   * according to the platform's default character encoding, and these bytes
   * are written in exactly the manner of the
   * <code>{@link #write(int)}</code> method.
   *
   * @param      obj   The <code>Object</code> to be printed
   * @see        java.lang.Object#toString()
   */
  public void print(Object obj) {
    write(String.valueOf(obj));
  }

  /* Methods that do terminate lines */
  /**
   * Terminates the current line by writing the line separator string.  The
   * line separator string is defined by the system property
   * <code>line.separator</code>, and is not necessarily a single newline
   * character (<code>'\n'</code>).
   */
  public void println() {
    newLine();
  }

  /**
   * Prints a boolean and then terminate the line.  This method behaves as
   * though it invokes <code>{@link #print(boolean)}</code> and then
   * <code>{@link #println()}</code>.
   *
   * @param x  The <code>boolean</code> to be printed
   */
  public void println(boolean x) {
    synchronized (this) {
      print(x);
      newLine();
    }
  }

  /**
   * Prints a character and then terminate the line.  This method behaves as
   * though it invokes <code>{@link #print(char)}</code> and then
   * <code>{@link #println()}</code>.
   *
   * @param x  The <code>char</code> to be printed.
   */
  public void println(char x) {
    synchronized (this) {
      print(x);
      newLine();
    }
  }

  /**
   * Prints an integer and then terminate the line.  This method behaves as
   * though it invokes <code>{@link #print(int)}</code> and then
   * <code>{@link #println()}</code>.
   *
   * @param x  The <code>int</code> to be printed.
   */
  public void println(int x) {
    synchronized (this) {
      print(x);
      newLine();
    }
  }

  /**
   * Prints a long and then terminate the line.  This method behaves as
   * though it invokes <code>{@link #print(long)}</code> and then
   * <code>{@link #println()}</code>.
   *
   * @param x  a The <code>long</code> to be printed.
   */
  public void println(long x) {
    synchronized (this) {
      print(x);
      newLine();
    }
  }

  /**
   * Prints a float and then terminate the line.  This method behaves as
   * though it invokes <code>{@link #print(float)}</code> and then
   * <code>{@link #println()}</code>.
   *
   * @param x  The <code>float</code> to be printed.
   */
  public void println(float x) {
    synchronized (this) {
      print(x);
      newLine();
    }
  }

  /**
   * Prints a double and then terminate the line.  This method behaves as
   * though it invokes <code>{@link #print(double)}</code> and then
   * <code>{@link #println()}</code>.
   *
   * @param x  The <code>double</code> to be printed.
   */
  public void println(double x) {
    synchronized (this) {
      print(x);
      newLine();
    }
  }

  /**
   * Prints an array of characters and then terminate the line.  This method
   * behaves as though it invokes <code>{@link #print(char[])}</code> and
   * then <code>{@link #println()}</code>.
   *
   * @param x  an array of chars to print.
   */
  public void println(char x[]) {
    synchronized (this) {
      print(x);
      newLine();
    }
  }

  /**
   * Prints a String and then terminate the line.  This method behaves as
   * though it invokes <code>{@link #print(String)}</code> and then
   * <code>{@link #println()}</code>.
   *
   * @param x  The <code>String</code> to be printed.
   */
  public void println(String x) {
    synchronized (this) {
      print(x);
      newLine();
    }
  }

  /**
   * Prints an Object and then terminate the line.  This method calls
   * at first String.valueOf(x) to get the printed object's string value,
   * then behaves as
   * though it invokes <code>{@link #print(String)}</code> and then
   * <code>{@link #println()}</code>.
   *
   * @param x  The <code>Object</code> to be printed.
   */
  public void println(Object x) {
    String s = String.valueOf(x);
    synchronized (this) {
      print(s);
      newLine();
    }
  }

  /**
   * A convenience method to write a formatted string to this output stream
   * using the specified format string and arguments.
   *
   * <p> An invocation of this method of the form <tt>out.printf(format,
   * args)</tt> behaves in exactly the same way as the invocation
   *
   * <pre>
   *     out.format(format, args) </pre>
   *
   * @param  format
   *         A format string as described in <a
   *         href="../util/Formatter.html#syntax">Format string syntax</a>
   *
   * @param  args
   *         Arguments referenced by the format specifiers in the format
   *         string.  If there are more arguments than format specifiers, the
   *         extra arguments are ignored.  The number of arguments is
   *         variable and may be zero.  The maximum number of arguments is
   *         limited by the maximum dimension of a Java array as defined by
   *         the <a href="http://java.sun.com/docs/books/vmspec/">Java
   *         Virtual Machine Specification</a>.  The behaviour on a
   *         <tt>null</tt> argument depends on the <a
   *         href="../util/Formatter.html#syntax">conversion</a>.
   *
   * @throws  IllegalFormatException
   *          If a format string contains an illegal syntax, a format
   *          specifier that is incompatible with the given arguments,
   *          insufficient arguments given the format string, or other
   *          illegal conditions.  For specification of all possible
   *          formatting errors, see the <a
   *          href="../util/Formatter.html#detail">Details</a> section of the
   *          formatter class specification.
   *
   * @throws  NullPointerException
   *          If the <tt>format</tt> is <tt>null</tt>
   *
   * @return  This output stream
   *
   * @since  1.5
   */
  public LogWriter printf(String format, Object... args) {
    return format(format, args);
  }

  /**
   * A convenience method to write a formatted string to this output stream
   * using the specified format string and arguments.
   *
   * <p> An invocation of this method of the form <tt>out.printf(l, format,
   * args)</tt> behaves in exactly the same way as the invocation
   *
   * <pre>
   *     out.format(l, format, args) </pre>
   *
   * @param  l
   *         The {@linkplain java.util.Locale locale} to apply during
   *         formatting.  If <tt>l</tt> is <tt>null</tt> then no localization
   *         is applied.
   *
   * @param  format
   *         A format string as described in <a
   *         href="../util/Formatter.html#syntax">Format string syntax</a>
   *
   * @param  args
   *         Arguments referenced by the format specifiers in the format
   *         string.  If there are more arguments than format specifiers, the
   *         extra arguments are ignored.  The number of arguments is
   *         variable and may be zero.  The maximum number of arguments is
   *         limited by the maximum dimension of a Java array as defined by
   *         the <a href="http://java.sun.com/docs/books/vmspec/">Java
   *         Virtual Machine Specification</a>.  The behaviour on a
   *         <tt>null</tt> argument depends on the <a
   *         href="../util/Formatter.html#syntax">conversion</a>.
   *
   * @throws  IllegalFormatException
   *          If a format string contains an illegal syntax, a format
   *          specifier that is incompatible with the given arguments,
   *          insufficient arguments given the format string, or other
   *          illegal conditions.  For specification of all possible
   *          formatting errors, see the <a
   *          href="../util/Formatter.html#detail">Details</a> section of the
   *          formatter class specification.
   *
   * @throws  NullPointerException
   *          If the <tt>format</tt> is <tt>null</tt>
   *
   * @return  This output stream
   *
   * @since  1.5
   */
  public LogWriter printf(Locale l, String format, Object... args) {
    return format(l, format, args);
  }

  /**
   * Writes a formatted string to this output stream using the specified
   * format string and arguments.
   *
   * <p> The locale always used is the one returned by {@link
   * java.util.Locale#getDefault() Locale.getDefault()}, regardless of any
   * previous invocations of other formatting methods on this object.
   *
   * @param  format
   *         A format string as described in <a
   *         href="../util/Formatter.html#syntax">Format string syntax</a>
   *
   * @param  args
   *         Arguments referenced by the format specifiers in the format
   *         string.  If there are more arguments than format specifiers, the
   *         extra arguments are ignored.  The number of arguments is
   *         variable and may be zero.  The maximum number of arguments is
   *         limited by the maximum dimension of a Java array as defined by
   *         the <a href="http://java.sun.com/docs/books/vmspec/">Java
   *         Virtual Machine Specification</a>.  The behaviour on a
   *         <tt>null</tt> argument depends on the <a
   *         href="../util/Formatter.html#syntax">conversion</a>.
   *
   * @throws  IllegalFormatException
   *          If a format string contains an illegal syntax, a format
   *          specifier that is incompatible with the given arguments,
   *          insufficient arguments given the format string, or other
   *          illegal conditions.  For specification of all possible
   *          formatting errors, see the <a
   *          href="../util/Formatter.html#detail">Details</a> section of the
   *          formatter class specification.
   *
   * @throws  NullPointerException
   *          If the <tt>format</tt> is <tt>null</tt>
   *
   * @return  This output stream
   *
   * @since  1.5
   */
  public LogWriter format(String format, Object... args) {
    synchronized (this) {
      if ((formatter == null)
              || (formatter.locale() != Locale.getDefault())) {
        formatter = new Formatter((Appendable) this);
      }
      formatter.format(Locale.getDefault(), format, args);
    }
    return this;
  }

  /**
   * Writes a formatted string to this output stream using the specified
   * format string and arguments.
   *
   * @param  l
   *         The {@linkplain java.util.Locale locale} to apply during
   *         formatting.  If <tt>l</tt> is <tt>null</tt> then no localization
   *         is applied.
   *
   * @param  format
   *         A format string as described in <a
   *         href="../util/Formatter.html#syntax">Format string syntax</a>
   *
   * @param  args
   *         Arguments referenced by the format specifiers in the format
   *         string.  If there are more arguments than format specifiers, the
   *         extra arguments are ignored.  The number of arguments is
   *         variable and may be zero.  The maximum number of arguments is
   *         limited by the maximum dimension of a Java array as defined by
   *         the <a href="http://java.sun.com/docs/books/vmspec/">Java
   *         Virtual Machine Specification</a>.  The behaviour on a
   *         <tt>null</tt> argument depends on the <a
   *         href="../util/Formatter.html#syntax">conversion</a>.
   *
   * @throws  IllegalFormatException
   *          If a format string contains an illegal syntax, a format
   *          specifier that is incompatible with the given arguments,
   *          insufficient arguments given the format string, or other
   *          illegal conditions.  For specification of all possible
   *          formatting errors, see the <a
   *          href="../util/Formatter.html#detail">Details</a> section of the
   *          formatter class specification.
   *
   * @throws  NullPointerException
   *          If the <tt>format</tt> is <tt>null</tt>
   *
   * @return  This output stream
   *
   * @since  1.5
   */
  public LogWriter format(Locale l, String format, Object... args) {
    synchronized (this) {
      if ((formatter == null)
              || (formatter.locale() != l)) {
        formatter = new Formatter((Appendable) this);
      }
      formatter.format(l, format, args);
    }
    return this;
  }

  /**
   * Appends the specified character sequence to this output stream.
   *
   * <p> An invocation of this method of the form <tt>out.append(csq)</tt>
   * behaves in exactly the same way as the invocation
   *
   * <pre>
   *     out.print(csq.toString()) </pre>
   *
   * <p> Depending on the specification of <tt>toString</tt> for the
   * character sequence <tt>csq</tt>, the entire sequence may not be
   * appended.  For instance, invoking then <tt>toString</tt> method of a
   * character buffer will return a subsequence whose content depends upon
   * the buffer's position and limit.
   *
   * @param  csq
   *         The character sequence to append.  If <tt>csq</tt> is
   *         <tt>null</tt>, then the four characters <tt>"null"</tt> are
   *         appended to this output stream.
   *
   * @return  This output stream
   *
   * @since  1.5
   */
  public LogWriter append(CharSequence csq) {
    if (csq == null) {
      print("null");
    } else {
      print(csq.toString());
    }
    return this;
  }

  /**
   * Appends a subsequence of the specified character sequence to this output
   * stream.
   *
   * <p> An invocation of this method of the form <tt>out.append(csq, start,
   * end)</tt> when <tt>csq</tt> is not <tt>null</tt>, behaves in
   * exactly the same way as the invocation
   *
   * <pre>
   *     out.print(csq.subSequence(start, end).toString()) </pre>
   *
   * @param  csq
   *         The character sequence from which a subsequence will be
   *         appended.  If <tt>csq</tt> is <tt>null</tt>, then characters
   *         will be appended as if <tt>csq</tt> contained the four
   *         characters <tt>"null"</tt>.
   *
   * @param  start
   *         The index of the first character in the subsequence
   *
   * @param  end
   *         The index of the character following the last character in the
   *         subsequence
   *
   * @return  This output stream
   *
   * @throws  IndexOutOfBoundsException
   *          If <tt>start</tt> or <tt>end</tt> are negative, <tt>start</tt>
   *          is greater than <tt>end</tt>, or <tt>end</tt> is greater than
   *          <tt>csq.length()</tt>
   *
   * @since  1.5
   */
  public LogWriter append(CharSequence csq, int start, int end) {
    CharSequence cs = (csq == null ? "null" : csq);
    write(cs.subSequence(start, end).toString());
    return this;
  }

  /**
   * Appends the specified character to this output stream.
   *
   * <p> An invocation of this method of the form <tt>out.append(c)</tt>
   * behaves in exactly the same way as the invocation
   *
   * <pre>
   *     out.print(c) </pre>
   *
   * @param  c
   *         The 16-bit character to append
   *
   * @return  This output stream
   *
   * @since  1.5
   */
  public LogWriter append(char c) {
    print(c);
    return this;
  }

  /*
   * The following private methods on the text- and character-output streams
   * always flush the stream buffers, so that writes to the underlying byte
   * stream occur as promptly as with the original PrintStream.
   */
  public void write(char buf[]) {
    sb.append(buf);
  }

  public void write(String s) {
    sb.append(s);
  }

  private void newLine() {
    sb.append(lineSeparator);
  }
}
