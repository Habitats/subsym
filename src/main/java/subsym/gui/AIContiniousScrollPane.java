package subsym.gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;

import subsym.Log;


/**
 * A continuous feed area, suitable for a continuous log. It will remove lines from the top when the maximum is
 * reached.
 *
 * @author Patrick
 */
public class AIContiniousScrollPane extends JScrollPane {

  private final int maxLength = 1000;

  private final JTextPane pane;
  private final SimpleAttributeSet set;
  private Font font;

  public AIContiniousScrollPane() {
    super(new JTextPane());
    pane = (JTextPane) getViewport().getView();
    pane.setEditable(true);
    pane.setFont(font);
    autoScroll(pane);
    pane.getDocument().addDocumentListener(new LimitLinesDocumentListener(maxLength));
//    setMinimumSize(new Dimension(100, 0));
//    setMaximumSize(new Dimension(300, 800));
    set = new SimpleAttributeSet();
//    StyleConstants.setFontFamily(set, Font.MONOSPACED);
//    StyleConstants.setFontSize(set, 10);
//    StyleConstants.setUnderline(set, true);
    setPreferredSize(new Dimension(0, 0));
  }

  @Override
  public void setFont(Font font) {
    this.font = font;
  }

  /**
   * this yields some bugs since it isn't entirely thread safe. gogo swing and concurrency... but yeah. doesn't fail
   * that often anyway, so lets just ignore exceptions for now TODO: fix this
   */
  public synchronized void append(String str) {
    try {
      Document doc = pane.getDocument();
      doc.insertString(doc.getLength(), str, set);
    } catch (BadLocationException e) {
      Log.v("Unable to append to scrollpane", e);
    }
  }

  /**
   * enable auto scrolling
   */
  private void autoScroll(JTextPane pane) {
    DefaultCaret caret = (DefaultCaret) pane.getCaret();
    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
  }

  public class LimitLinesDocumentListener implements DocumentListener {

    private int maximumLines;
    private final boolean isRemoveFromStart;

    /*
     * Specify the number of lines to be stored in the Document. Extra lines will be removed from the
     * start of the Document.
     */
    public LimitLinesDocumentListener(int maximumLines) {
      this(maximumLines, true);
    }

    /*
     * Specify the number of lines to be stored in the Document. Extra lines will be removed from the
     * start or end of the Document, depending on the boolean value specified.
     */
    public LimitLinesDocumentListener(int maximumLines, boolean isRemoveFromStart) {
      setLimitLines(maximumLines);
      this.isRemoveFromStart = isRemoveFromStart;
    }

    /*
     * Return the maximum number of lines to be stored in the Document
     */
    public int getLimitLines() {
      return maximumLines;
    }

    /*
     * Set the maximum number of lines to be stored in the Document
     */
    public void setLimitLines(int maximumLines) {
      if (maximumLines < 1) {
        String message = "Maximum lines must be greater than 0";
        throw new IllegalArgumentException(message);
      }

      this.maximumLines = maximumLines;
    }

    // Handle insertion of new text into the Document

    @Override
    public void insertUpdate(final DocumentEvent e) {
      // Changes to the Document can not be done within the listener
      // so we need to put the processing to the end of the EDT

      SwingUtilities.invokeLater(() -> removeLines(e));
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    /*
     * Remove lines from the Document when necessary
     */
    private void removeLines(DocumentEvent e) {
      // The root Element of the Document will tell us the total number
      // of line in the Document.

      Document document = e.getDocument();
      Element root = document.getDefaultRootElement();

      while (root.getElementCount() > maximumLines) {
        if (isRemoveFromStart) {
          removeFromStart(document, root);
        } else {
          removeFromEnd(document, root);
        }
      }
    }

    /*
     * Remove lines from the start of the Document
     */
    private void removeFromStart(Document document, Element root) {
      Element line = root.getElement(0);
      int end = line.getEndOffset();

      try {
        document.remove(0, end);
      } catch (BadLocationException e) {
        Log.v("Unable to remove from top of scrollpane", e);
      }
    }

    /*
     * Remove lines from the end of the Document
     */
    private void removeFromEnd(Document document, Element root) {
      // We use start minus 1 to make sure we remove the newline
      // character of the previous line

      Element line = root.getElement(root.getElementCount() - 1);
      int start = line.getStartOffset();
      int end = line.getEndOffset();

      try {
        document.remove(start - 1, end - start);
      } catch (BadLocationException e) {
        Log.v("Unable to remove from end of scrollpane", e);
      }
    }
  }
}
