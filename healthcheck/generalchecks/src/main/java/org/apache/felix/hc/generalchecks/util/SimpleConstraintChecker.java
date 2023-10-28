/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.felix.hc.generalchecks.util;

import java.util.Arrays;
import java.util.Calendar;

import org.apache.felix.hc.core.impl.util.lang.StringUtils;

/** Simple check of values against expressions like &lt; N, &gt; N, between two values etc. See the SimpleConstraintCheckerTest for
 * examples. */
public class SimpleConstraintChecker {

    public static final String GREATER_THAN = ">";
    public static final String LESS_THAN = "<";
    public static final String EQUALS = "=";

    public static final String BETWEEN = "between";
    public static final String AND = "and";
    public static final String CONTAINS = "contains";
    public static final String STARTS_WITH = "starts_with";
    public static final String ENDS_WITH = "ends_with";
    public static final String MATCHES = "matches"; // regex
    public static final String OLDER_THAN = "older_than"; // for unix timestamps 

    /** Check value against expression and report to result 
     * @param inputValue the value to check
     * @param constraint the constraint to check the value against
     * @return true if constraint is met
     *  */
    public boolean check(Object inputValue, String constraint) throws NumberFormatException {

        if(inputValue == null) {
            return false;
        }
        
        final String stringValue = inputValue == null ? "" : inputValue.toString();

        if (StringUtils.isBlank(constraint)) {
            throw new IllegalArgumentException("Empty constraint, cannot evaluate");
        }

        String[] parts = constraint.split(" +");
        boolean matches = false;
        boolean inverseResult = false;
        if(parts[0].equalsIgnoreCase("not")) {
            inverseResult = true;
            String[] newParts = new String[parts.length - 1];
            System.arraycopy(parts, 1, newParts, 0, newParts.length);
            parts = newParts;
        }
        
        if (parts[0].equals(GREATER_THAN) && parts.length == 2) {
            long value = Long.parseLong(stringValue);
            matches = value > Long.valueOf(parts[1]);

        } else if (parts[0].equals(LESS_THAN) && parts.length == 2) {
            long value = Long.parseLong(stringValue);
            matches = value < Long.valueOf(parts[1]);

        } else if (parts[0].equals(EQUALS) && parts.length == 2) {
            Long longValue;
            if((longValue = getLongObject(stringValue)) != null) {
                matches = longValue.longValue() == Long.parseLong(parts[1]);
            } else {
                matches = stringValue.equals(parts[1]);
            }
        } else if (parts.length == 4 && BETWEEN.equalsIgnoreCase(parts[0]) && AND.equalsIgnoreCase(parts[2])) {
            long value = Long.parseLong(stringValue);
            long lowerBound = Long.parseLong(parts[1]);
            long upperBound = Long.parseLong(parts[3]);
            matches = value > lowerBound && value < upperBound;

        } else if (parts.length > 1 && CONTAINS.equalsIgnoreCase(parts[0])) {
            String pattern = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
            matches = stringValue.contains(pattern);
        } else if (parts.length > 1 && STARTS_WITH.equalsIgnoreCase(parts[0])) {
            String pattern = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
            matches = stringValue.startsWith(pattern);
        } else if (parts.length > 1 && ENDS_WITH.equalsIgnoreCase(parts[0])) {
            String pattern = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
            matches = stringValue.endsWith(pattern);
        } else if (parts.length > 1 && MATCHES.equalsIgnoreCase(parts[0])) {
            String pattern = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
            matches = stringValue.matches(pattern);
        } else if (parts.length > 1 && OLDER_THAN.equalsIgnoreCase(parts[0]) && parts.length == 3) {
            int unit = stringToUnit(parts[2]);
            long timestamp = Long.parseLong(stringValue);
            int timeDiff = Integer.parseInt(parts[1]);
            
            Calendar cal = Calendar.getInstance();
            cal.add(unit, -timeDiff);
            long compareTimestamp = cal.getTime().getTime();
            
            matches = timestamp < compareTimestamp;

        } else {
            matches = String.join("", parts).equals(stringValue);
        }

        return matches ^ inverseResult;

    }

    private int stringToUnit(String unitString) {
        int unit;
        switch(unitString) {
        case "ms": 
            unit = Calendar.MILLISECOND; break;
        case "min": 
        case "minute": 
        case "minutes": 
            unit = Calendar.MINUTE; break;
        case "h": 
        case "hour": 
        case "hours": 
            unit = Calendar.HOUR; break;
        case "d": 
        case "day": 
        case "days": 
            unit = Calendar.DAY_OF_YEAR; break;
        case "s": 
        case "sec": 
        case "second": 
        case "seconds": 
            unit = Calendar.SECOND; break;
        default:
            throw new IllegalArgumentException("Unexpected unit '"+unitString+"'");
        }
        return unit;
    }
    
    private static Long getLongObject(String strNum) {
        if (strNum == null) {
            return null;
        }
        try {
            return Long.valueOf(strNum);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }
}