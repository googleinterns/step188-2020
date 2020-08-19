package com.google.sps.data;

/** Class that represents a keyword using in search ranking. */
public class Keyword {
  private String name;
  private float relevance;
  public Keyword(String name, float relevance) {
    this.name = name;
    this.relevance = relevance;
  }

  public String getName() {
    return name;
  }
  
  public float getRelevance() {
    return relevance;
  }
}