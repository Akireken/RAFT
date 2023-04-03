package octo.raft.entity;

import java.util.Objects;

public class Entry {
  private String value;
  private int term;

  public Entry(String value, int terme) {
    this.value = value;
    this.term = terme;
  }

  public Entry() {}


  public String getValue() {
    return this.value;
  }

  public boolean isHeartbeat(){
    return this.value == null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Entry entry = (Entry) o;
    return term == entry.term && Objects.equals(value, entry.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, term);
  }

  public int getTerm() {
    return this.term;
  }
}
