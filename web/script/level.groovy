/* $Id$ */
import groovy.xml.MarkupBuilder
import games.stendhal.common.Level;
// default content-type is text/html


mb = new MarkupBuilder(new PrintWriter(out))
mb.table( cellspacing : "4", width : "100%") { 
  tr () {
    th(align : "right", "Level");
    th(align : "right", "Experience");
    td(width : "3");
    th(align : "right", "Level");
    th(align : "right", "Experience");
    td(width : "3");
    th(align : "right", "Level");
    th(align : "right", "Experience");
    td(width : "3");
    th(align : "right", "Level");
    th(align : "right", "Experience");
  }
  int num = (Level.maxLevel() / 4).intValue() + 1;
  for( i in 0..(num-1)) {
    tr () {
      td(align : "right", "" + i);
      td(align : "right", "" + Level.getXP(i));
      td();
      td(align : "right", "" + (i + num));
      td(align : "right", "" + Level.getXP(i + num));
      td();
      td(align : "right", "" + (i + 2 * num));
      td(align : "right", "" + Level.getXP(i + 2 * num));
      td();
      td(align : "right", "" + (i + 3 * num));
      td(align : "right", "" + Level.getXP(i + 3 * num));
    }
  }
}
