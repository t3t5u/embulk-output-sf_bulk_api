package org.embulk.output.sf_bulk_api;

import com.sforce.soap.partner.sobject.SObject;
import java.util.Locale;
import org.embulk.spi.Column;
import org.embulk.spi.ColumnVisitor;
import org.embulk.spi.PageReader;
import org.embulk.spi.time.Timestamp;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class SForceColumnVisitor implements ColumnVisitor {
  private final SObject record;
  private final PageReader pageReader;
  private static int MILLISECOND = 1000;

  public SForceColumnVisitor(SObject record, PageReader pageReader) {
    this.record = record;
    this.pageReader = pageReader;
  }

  @Override
  public void booleanColumn(Column column) {
    record.addField(column.getName(), pageReader.getBoolean(column));
  }

  @Override
  public void longColumn(Column column) {
    record.addField(column.getName(), Long.toString(pageReader.getLong(column)));
  }

  @Override
  public void doubleColumn(Column column) {
    record.addField(column.getName(), Double.toString(pageReader.getDouble(column)));
  }

  @Override
  public void stringColumn(Column column) {
    record.addField(column.getName(), pageReader.getString(column));
  }

  @Override
  public void timestampColumn(Column column) {
    Timestamp timestamp = pageReader.getTimestamp(column);
    if (timestamp != null) {
      DateTime dateTime = new DateTime(timestamp.getEpochSecond() * MILLISECOND, DateTimeZone.UTC);
      record.addField(column.getName(), dateTime.toCalendar(Locale.ENGLISH));
    } else {
      record.addField(column.getName(), null);
    }
  }

  @Override
  public void jsonColumn(Column column) {
    record.addField(column.getName(), pageReader.getString(column));
  }
}
