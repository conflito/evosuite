/**
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.regression;

import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.evosuite.Properties;

/**
 * Implementation of object distance following
 * "Object distance and its application to adaptive random testing of object-oriented programs"
 * by Ilinca Ciupa, Andreas Leitner, Manuel Oriol, Bertrand Meyer (<a
 * href="http://se.ethz.ch/~meyer/publications/testing/object_distance.pdf"
 * >http://se.ethz.ch/~meyer/publications/testing/object_distance.pdf</a>).
 *
 * We implemented the following changes:
 * <ul>
 * <li>In the paper if a reference field does not match (i.e., is different
 * because the types are different) then R = 10 is used as difference. This does
 * not make sense for several reasons:
 * <ul>
 * <li>The difference is already taken into account in the type distance.</li>
 * <li>If one type adds a field but the other does not, so what? R/2?</li>
 * </ul>
 * Therefore we decided to apply the value R as factor in the type difference to
 * the non-shared fields.</li>
 * <li>In the paper a factor of 1/2 is applied to the recursive distance. We
 * treat the recursive distance simply as any other field distance, thus it is
 * normalized by the number of fields and not by a static factor.</li>
 * <li>There is no distance given for two characters. We defined that to be C =
 * 10.</li>
 * </ul>
 */
public class ObjectDistanceCalculator {

	private static final Logger logger = LoggerFactory.getLogger(ObjectDistanceCalculator.class);
	private static final double B = 1;
	private static final int MAX_RECURSION = 100;
	private final Map<Integer, Integer> hashRecursionCntMap = new LinkedHashMap<>();
	private final Map<Integer, Double> resultCache = new LinkedHashMap<>();

	private int numDifferentVariables = 0;

	public static double getObjectDistance(Object p, Object q) {
		ObjectDistanceCalculator calculator = new ObjectDistanceCalculator();
		return calculator.getObjectDistanceImpl(p, q) + normalize(calculator.numDifferentVariables);
	}

	private static double getElementaryDistance(Boolean p, Boolean q) {
		if (p.equals(q)) {
			return 0;
		}
		return B;
	}

	private static double normalize(double x) {
		return x / (x + 1.0);
	}

	private static Integer getHasCode(Object p, Object q) {
		return ((p == null) ? 0 : p.hashCode()) + ((q == null) ? 0 : q.hashCode());
	}

	private double getElementaryDistance(Character p, Character q) {
		if (p.equals(q)) {
			return 0;
		} else {
			numDifferentVariables++;
		}

		return normalize(Math.abs(p - q));
	}

	private double getElementaryDistance(Number p, Number q) {
		if (!p.equals(q)) {
			numDifferentVariables++;
		}

		if ((p instanceof Double) && (((Double) p).isNaN() || ((Double) p).isInfinite())) {
			if (p.equals(q)) {
				return 0;
			} else {
				return 1;
			}
		}

		if ((p instanceof Float) && (((Float) p).isNaN() || ((Float) p).isInfinite())) {
			if (p.equals(q)) {
				return 0;
			} else {
				return 1;
			}
		}

		double distance;
		if (p instanceof Long) {
			distance = Math.abs(p.longValue() - q.longValue());
		} else {
			distance = Math.abs(p.doubleValue() - q.doubleValue());
		}

		// If the epsilon is less than 0.01D (as is used for assertion generation)
		// set distance to 0.
		if (p instanceof Double) {
			if (distance < 0.01) {
				distance = 0;
			}
		}

		return normalize(distance);
	}

	/* Levenshtein distance */
	private double getElementaryDistance(String p, String q) {
		if (!p.equals(q)) {
			numDifferentVariables++;
		}
		int[][] distanceMatrix = new int[p.length() + 1][q.length() + 1];
		for (int idx = 0; idx <= p.length(); idx++) {
			distanceMatrix[idx][0] = idx;
		}
		for (int jdx = 1; jdx <= q.length(); jdx++) {
			distanceMatrix[0][jdx] = jdx;
		}
		for (int idx = 1; idx <= p.length(); idx++) {
			for (int jdx = 1; jdx <= q.length(); jdx++) {
				int cost;
				if (p.charAt(idx - 1) == q.charAt(jdx - 1)) {
					cost = 0;
				} else {
					cost = 1;
				}
				distanceMatrix[idx][jdx] = Math.min(
						distanceMatrix[idx - 1][jdx] + 1, // deletion
						Math.min(distanceMatrix[idx][jdx - 1] + 1, // insertion
								distanceMatrix[idx - 1][jdx - 1] + cost // substitution
								));
				if ((idx > 1) && (jdx > 1)
						&& (p.charAt(idx - 1) == q.charAt(jdx - 2))
						&& (p.charAt(idx - 2) == q.charAt(jdx - 1))) {
					distanceMatrix[idx][jdx] = Math.min(
							distanceMatrix[idx][jdx],
							distanceMatrix[idx - 2][jdx - 2] + cost // transposition
							);
				}
			}
		}
		return normalize(distanceMatrix[p.length()][q.length()]);
	}

	private double getObjectDistanceImpl(Object p, Object q) {
		if (p == q) {
			return 0.0;
		}

		// one is null, the other isn't
		if (p == null || q == null) {
			// if only one of them is null
			numDifferentVariables++;
			return 0;
		}

		// type mismatch
		boolean isNumberP = p instanceof Number;
		boolean isNumberQ = q instanceof Number;
		if (isNumberP != isNumberQ) {
			return 1;
		}

		// if they're both numbers, check NaN / Infinity status
		if (isNumberP && haveDifferentNaNOrInfinity(p, q)) {
			return 1;
		}

		// if they're from different classes
		if (!p.getClass().getName().equals(q.getClass().getName())) {
			numDifferentVariables++;
			return 0;
		}

		// What if one is a primitive and the other not?
		if (p instanceof Number) {
			if(Properties.SHOW_DISTANCE)
				logger.warn("Calculating distance between Number fields.");
			
			return getElementaryDistance((Number) p, (Number) q);
		}

		if (p instanceof Boolean) {
			if(Properties.SHOW_DISTANCE)
				logger.warn("Calculating distance between Boolean fields.");
			
			return getElementaryDistance((Boolean) p, (Boolean) q);
		}

		if (p instanceof String) {
			if(Properties.SHOW_DISTANCE)
				logger.warn("Calculating distance between String fields.");
			
			return getElementaryDistance((String) p, (String) q);
		}

		if (p instanceof Character) {
			if(Properties.SHOW_DISTANCE)
				logger.warn("Calculating distance between Character fields.");
			
			return getElementaryDistance((Character) p, (Character) q);
		}

		if (p instanceof Map && isStringObjectMap((Map) p) && isStringObjectMap((Map) q)) {
			if(Properties.SHOW_DISTANCE)
				logger.warn("Calculating distance between Map fields.");
			
			return normalize(getObjectMapDistance((Map<String, Object>) p, (Map<String, Object>) q));
		}

		/*
    TODO: add support for maps of other types.
    One possible approach is perhaps to turn the object into json and turning it back to a
    recursive Map<String,Object> and apply the method above to it. (Jackson library is able
    to do this pretty quickly).
		 */

		if (p instanceof Enum) {
			if(Properties.SHOW_DISTANCE)
				logger.warn("Calculating distance between ENUM fields.");
			
			// Levenshtein distance of enum name
			return getElementaryDistance(((Enum) p).name(), ((Enum) q).name());
		}
		
		if(Properties.SHOW_DISTANCE)
			logger.warn("Calculating distance between composite fields.");
		
		return getCompositeObjectDistance(p, q);

		// throw new Error("Distance of unknown type!");
	}

	/**
	 * This following relatively-hacky way, checks whether the map keys are strings
	 */
	private boolean isStringObjectMap(Map p) {
		if(p.isEmpty()){
			return true;
		}
		return p.keySet().iterator().next().getClass().getName().equals(String.class.getName());
	}

	private boolean haveDifferentNaNOrInfinity(Object p, Object q) {
		boolean isNanP = false;
		boolean isNanQ = false;

		boolean isInfiniteP = false;
		boolean isInfiniteQ = false;

		if (p instanceof Double) {
			Double doubleP = (Double) p;
			isNanP = Double.isNaN(doubleP);
			isInfiniteP = Double.isInfinite(doubleP);
		}

		if (q instanceof Double) {
			Double doubleQ = (Double) q;
			isNanQ = Double.isNaN(doubleQ);
			isInfiniteQ = Double.isInfinite(doubleQ);
		}

		if (p instanceof Float) {
			Float floatP = (Float) p;
			isNanP = Float.isNaN(floatP);
			isInfiniteP = Float.isInfinite(floatP);
		}

		if (q instanceof Float) {
			Float floatQ = (Float) q;
			isNanQ = Float.isNaN(floatQ);
			isInfiniteQ = Float.isInfinite(floatQ);
		}

		// One is NaN, other is Infinity
		if (isNanP && isInfiniteQ || isNanQ && isInfiniteP) {
			return true;
		}

		// one is Nan, the other isn't
		if (isNanP != isNanQ) {
			return true;
		}

		// one is infinite, the other isn't
		if (isInfiniteP != isInfiniteQ) {
			return true;
		}

		/*
      both are infinite (previous condition ensures equality), and they don't match
      (e.g., one is positive infinity, and the other is negative)
		 */
		if (isInfiniteP && !p.equals(q)) {
			return true;
		}

		return false;
	}

	public double getObjectMapDistance(Map<String, Object> map1, Map<String, Object> map2) {
		double distance = 0.0;
		int missingFields = 0;

		for (String fieldName : map1.keySet()) {
			if (!map2.containsKey(fieldName)) {
				missingFields++;
				continue;
			}
			Object value1 = map1.get(fieldName);
			Object value2 = map2.get(fieldName);
			double tmpDistance = 0;
			try {
				if(Properties.SHOW_DISTANCE)
					logger.warn("Calculating distance between field " + 
							fieldName + " with values " + value1.toString() + 
							" and " + value2.toString());
				
				tmpDistance = getObjectDistanceImpl(value1, value2);
				
				if(Properties.SHOW_DISTANCE)
					logger.warn("Distance between field " + 
							fieldName + " with values " + value1.toString() + 
							" and " + value2.toString() + ": " + tmpDistance);
				
				
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}

			if (Double.valueOf(tmpDistance).isNaN() || Double.valueOf(tmpDistance).isInfinite()) {
				numDifferentVariables++;
				tmpDistance = 0;
			}

			distance += tmpDistance;
		}

		// account for field differences
		distance += getElementaryDistance(map1.size(), map2.size());
		if (map1.size() == map2.size()) {
			distance += normalize(missingFields);
		}

		return distance;
	}

	private boolean breakRecursion(Object p, Object q) {
		Integer hashCode = getHasCode(p, q);
		Integer recursionCount = hashRecursionCntMap.get(hashCode);
		if (recursionCount == null) {
			recursionCount = 0;
		}
		if (recursionCount >= MAX_RECURSION) {
			return true;
		}
		recursionCount++;
		hashRecursionCntMap.put(hashCode, recursionCount);
		return false;
	}

	/**
	 * Calculates the distance between two complex (not number or string) objects
	 * @param p - the first object
	 * @param q - the second object
	 * @return - the distance between them
	 */
	private double getCompositeObjectDistance(Object p, Object q) {
		Double cachedDistance = resultCache.get(getHasCode(p, q));

		if (cachedDistance != null) {
			if(Properties.SHOW_DISTANCE)
				logger.warn("Using cached distance for " + 
					p.getClass().getCanonicalName() + " and " +
					q.getClass().getCanonicalName() + ":" + cachedDistance);

			return cachedDistance;
		}
		if (breakRecursion(p, q)) {
			if(Properties.SHOW_DISTANCE)
				logger.warn("Breaking recursion.");
			
			return 0.0;
		}
		double distance = getObjectMapDistance(getFieldMap(p), getFieldMap(q));
		String pName = p.getClass().getCanonicalName();
		String qName = q.getClass().getCanonicalName();
		distance += getElementaryDistance(pName, qName);

		if(Properties.SHOW_DISTANCE)
			logger.warn("Composite distance for " + 
					p.getClass().getCanonicalName() + " and " +
					q.getClass().getCanonicalName() + ":" + distance);		

		resultCache.put(getHasCode(p, q), distance);
		return distance;
	}

	/**
	 * Calculates a map with the fields' names and values of a given object
	 * @param o - the object in question
	 * @return the map of fields and values
	 */
	private Map<String, Object> getFieldMap(Object o){
		Map<String, Object> result = new LinkedHashMap<>();
		if(o != null) {
			for(Field f: o.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				String fieldName = f.getName();
				try {
					Object value = f.get(o);
					result.put(fieldName, value);
				} catch (IllegalArgumentException | IllegalAccessException e) {}
			}
		}
		return result;
	}

	public int getNumDifferentVariables() {
		return numDifferentVariables;
	}
}
