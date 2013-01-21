/**
 * Copyright (c) 2012-2012 Malhar, Inc. All rights reserved.
 */
package com.malhartech.lib.math;

import com.malhartech.engine.TestSink;
import java.util.HashMap;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Functional tests for {@link com.malhartech.lib.math.ChangeMap}<p>
 *
 */
public class ChangeMapTest
{
  private static Logger log = LoggerFactory.getLogger(ChangeMapTest.class);

  /**
   * Test node logic emits correct results
   */
  @Test
  @SuppressWarnings("SleepWhileInLoop")
  public void testNodeProcessing() throws Exception
  {
    testNodeProcessingSchema(new ChangeMap<String, Integer>());
    testNodeProcessingSchema(new ChangeMap<String, Double>());
    testNodeProcessingSchema(new ChangeMap<String, Float>());
    testNodeProcessingSchema(new ChangeMap<String, Short>());
    testNodeProcessingSchema(new ChangeMap<String, Long>());
  }

  public void testNodeProcessingSchema(ChangeMap oper)
  {
    TestSink changeSink = new TestSink();
    TestSink percentSink = new TestSink();

    oper.change.setSink(changeSink);
    oper.percent.setSink(percentSink);

    oper.beginWindow(0);
    HashMap<String, Number> input = new HashMap<String, Number>();
    input.put("a", 2);
    input.put("b", 10);
    input.put("c", 100);
    oper.base.process(input);

    input.clear();
    input.put("a", 3);
    input.put("b", 2);
    input.put("c", 4);
    oper.data.process(input);

    input.clear();
    input.put("a", 4);
    input.put("b", 19);
    input.put("c", 150);
    oper.data.process(input);

    oper.endWindow();

    // One for each key
    Assert.assertEquals("number emitted tuples", 6, changeSink.collectedTuples.size());
    Assert.assertEquals("number emitted tuples", 6, percentSink.collectedTuples.size());

    double aval = 0;
    double bval = 0;
    double cval = 0;
    log.debug("\nLogging tuples");
    for (Object o: changeSink.collectedTuples) {
      HashMap<String, Number> map = (HashMap<String, Number>)o;
      Assert.assertEquals("map size", 1, map.size());
      Number anum = map.get("a");
      Number bnum = map.get("b");
      Number cnum = map.get("c");
      if (anum != null) {
        aval += anum.doubleValue();
      }
      if (bnum != null) {
        bval += bnum.doubleValue();
      }
      if (cnum != null) {
        cval += cnum.doubleValue();
      }
    }
    Assert.assertEquals("change in a", 3.0, aval);
    Assert.assertEquals("change in a", 1.0, bval);
    Assert.assertEquals("change in a", -46.0, cval);

    aval = 0.0;
    bval = 0.0;
    cval = 0.0;

    for (Object o: percentSink.collectedTuples) {
      HashMap<String, Number> map = (HashMap<String, Number>)o;
      Assert.assertEquals("map size", 1, map.size());
      Number anum = map.get("a");
      Number bnum = map.get("b");
      Number cnum = map.get("c");
      if (anum != null) {
        aval += anum.doubleValue();
      }
      if (bnum != null) {
        bval += bnum.doubleValue();
      }
      if (cnum != null) {
        cval += cnum.doubleValue();
      }
    }
    Assert.assertEquals("change in a", 150.0, aval);
    Assert.assertEquals("change in a", 10.0, bval);
    Assert.assertEquals("change in a", -46.0, cval);
  }
}