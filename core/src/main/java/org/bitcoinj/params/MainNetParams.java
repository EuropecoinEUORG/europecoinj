/*
 * Copyright 2013 Google Inc.
 * Copyright 2015 Andreas Schildbach
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

package org.bitcoinj.params;

import org.bitcoinj.core.*;
import org.bitcoinj.net.discovery.*;

import java.net.*;
import java.math.BigInteger;

import static com.google.common.base.Preconditions.*;

/**
 * Parameters for the main production network on which people trade goods and services.
 */
public class MainNetParams extends AbstractBitcoinNetParams {
    public static final int MAINNET_MAJORITY_WINDOW = 1000;
    public static final int MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED = 950;
    public static final int MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE = 750;

    public MainNetParams() {
        super();
        interval = INTERVAL;
        targetTimespan = TARGET_TIMESPAN;
        dumpedPrivateKeyHeader = 40 + 128;
        addressHeader = 33;
        p2shHeader = 5;
        acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        port = 8881;
        packetMagic = 0x4555524fL;
        bip32HeaderPub = 0x0488B21E; //The 4 byte header that serializes in base58 to "xpub".
        bip32HeaderPriv = 0x0488ADE4; //The 4 byte header that serializes in base58 to "xprv"

        majorityEnforceBlockUpgrade = MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = MAINNET_MAJORITY_WINDOW;

        genesisBlock.setDifficultyTarget(0x21000FFF);
        genesisBlock.setTime(1468016542);
        genesisBlock.setNonce(1767);
        genesisBlock.setStartLocation(189030);
        genesisBlock.setFinalCaclucation(1494081267);

        id = ID_MAINNET;
        subsidyDecreaseBlockCount = 819678;
        spendableCoinbaseDepth = 288;
        String genesisHash = genesisBlock.getHashAsString();
        checkState(genesisHash.equals("000d0da26987ead011c5d568e627f7e3d4a4f83a0b280b1134d8e7e366377f9a"),
                genesisHash);

        // This contains (at a minimum) the blocks which are not BIP30 compliant. BIP30 changed how duplicate
        // transactions are handled. Duplicated transactions could occur in the case where a coinbase had the same
        // extraNonce and the same outputs but appeared at different heights, and greatly complicated re-org handling.
        // Having these here simplifies block connection logic considerably.
        checkpoints.put(1,     Sha256Hash.wrap("003e216e51eade2b40e1cb56695543f8ee5f8d6a7c080b13ea2c33c02a27747a"));
        checkpoints.put(3724,  Sha256Hash.wrap("000000561a9d683eb9e96e6aa0daeb796f09c3e6f734f88b745bb32d9b3ca84e"));
        checkpoints.put(7614,  Sha256Hash.wrap("0000004eb7ffab1324ac772ee5b70b86d32375c0ad309ed315283404f50a9a49"));
        checkpoints.put(10176, Sha256Hash.wrap("000000eac0ce8e159aff91f656f6aa75af624246b69db7860bee21d3055e1cdd"));
        checkpoints.put(13104, Sha256Hash.wrap("000000727f3e125d4990effe146f749180419409a9a7388647139bb31a10368f"));
        checkpoints.put(14726, Sha256Hash.wrap("00000000028e2165069b61452056157febe10437c47667bf6631b99721e9d4d7"));

        dnsSeeds = new String[] {
            "85.214.68.75",
            "37.120.190",
        };
    }

    private static MainNetParams instance;
    public static synchronized MainNetParams get() {
        if (instance == null) {
            instance = new MainNetParams();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return PAYMENT_PROTOCOL_ID_MAINNET;
    }
}
