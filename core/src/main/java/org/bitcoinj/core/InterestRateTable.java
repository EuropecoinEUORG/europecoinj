/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitcoinj.core;

import com.google.common.base.Objects;
import org.bitcoinj.script.*;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.*;

import javax.annotation.*;
import java.io.*;
import java.math.*;
import java.util.*;

import static com.google.common.base.Preconditions.*;


public class InterestRateTable {

    private final int ONEDAY = 288;
    private final int THIRTYDAYS = ONEDAY * 30;
    private final int ONEYEAR = ONEDAY * 365;
    private final int ONEYEARPLUS1 = ONEYEAR + 1;
    private final int TWOYEARS = ONEYEAR * 2;

    private long rateTable[]; //ONEYEARPLUS1];
    private long bonusTable[]; //ONEYEARPLUS1];

    public InterestRateTable() {
        rateTable = new long[ONEYEARPLUS1];
        bonusTable = new long[ONEYEARPLUS1];

        long rate_sum = 0;
        long bonus_sum = 0;

        rateTable[0] = 1;
        rateTable[0] = rateTable[0] << 62;
        bonusTable[0] = 1;
        bonusTable[0] = bonusTable[0] << 54;

        for(int i = 1; i < ONEYEARPLUS1; i++) {
            rateTable[i] = rateTable[i-1] + (rateTable[i-1] >> 22);
            bonusTable[i] = bonusTable[i-1] + (bonusTable[i-1] >> 16);

            rate_sum ^= rateTable[i];
            bonus_sum ^= bonusTable[i];
        }

        if (rate_sum != 74832654732641894L)
            throw new VerificationException("Rate table chksum is wrong: " + rate_sum);

        if (bonus_sum != 34694287198574077L)
            throw new VerificationException("Bonus table chksum is wrong: " + bonus_sum);
    }

    private Coin getBonusForAmount(int periods, Coin theAmount) {
        BigInteger amount256 = BigInteger.valueOf(theAmount.getValue());
        BigInteger rate256 = BigInteger.valueOf(bonusTable[periods]);
        BigInteger rate0256 = BigInteger.valueOf(bonusTable[0]);
        BigInteger result = amount256.multiply(rate256).divide(rate0256);
        return Coin.valueOf(result.longValue()).minus(theAmount);
    }

    private Coin getRateForAmount(int periods, Coin theAmount) {
        BigInteger amount256 = BigInteger.valueOf(theAmount.getValue());
        BigInteger rate256 = BigInteger.valueOf(rateTable[periods]);
        BigInteger rate0256 = BigInteger.valueOf(rateTable[0]);
        BigInteger result = amount256.multiply(rate256).divide(rate0256);
        return Coin.valueOf(result.longValue()).minus(theAmount);
    }

    public Coin GetInterest(Coin nValue, int outputBlockHeight, int valuationHeight, int maturationBlock) {
        //These conditions generally should not occur
        if(maturationBlock >= 500000000 || outputBlockHeight < 0 || valuationHeight < 0 || valuationHeight < outputBlockHeight)
            return nValue;

        //Regular deposits can have a maximum of 30 days interest
        int blocks = Math.min(THIRTYDAYS, valuationHeight - outputBlockHeight);

        //Term deposits may have up to 1 year of interest
        if(maturationBlock > 0)
            blocks = Math.min(ONEYEAR, valuationHeight - outputBlockHeight);

        Coin standardInterest = getRateForAmount(blocks, nValue);
        Coin bonusAmount = Coin.ZERO;
        Coin interestAmount = standardInterest.add(bonusAmount);
        Coin termDepositAmount = Coin.ZERO;

        //Reward term deposits more
        if(maturationBlock > 0) {
            int term = Math.min(ONEYEAR, maturationBlock - outputBlockHeight);
            //No advantage to term deposits of less than 2 days
            if(term > ONEDAY * 2){
                BigInteger am = BigInteger.valueOf(interestAmount.getValue());
                BigInteger fac = BigInteger.valueOf(TWOYEARS - term);
                BigInteger div = BigInteger.valueOf(TWOYEARS);
                BigInteger result = am.subtract(am.multiply(fac.pow(6)).divide(div.pow(6)));
                termDepositAmount = Coin.valueOf(result.longValue());
            }
        }

        return nValue.add(interestAmount).add(termDepositAmount);
    }

    private static InterestRateTable instance;
    public static synchronized InterestRateTable get() {
        if (instance == null) {
            instance = new InterestRateTable();
        }
        return instance;
    }
}
