/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jeco.core.util;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author jlrisco
 */
public class Maths {

  public static double sum(List<Double> numbers) {
    double res = 0;
    for (Double number : numbers) {
      res += number;
    }
    return res;
  }

  public static double mean(List<Double> numbers) {
    if (numbers.isEmpty()) {
      return 0;
    }
    double res = sum(numbers) / numbers.size();
    return res;
  }

  public static double median(List<Double> numbers) {
    Collections.sort(numbers);
    int middle = numbers.size() / 2;
    if (numbers.size() % 2 == 1) {
      return numbers.get(middle);
    } else {
      return (numbers.get(middle - 1) + numbers.get(middle)) / 2.0;
    }
  }

  public static double std(List<Double> numbers) {
    double res = 0;
    double avg = mean(numbers);
    for(Double number : numbers) {
      res += Math.pow(number-avg, 2);
    }
    res = Math.sqrt(res/(numbers.size()-1));
    return res;
  }
}
