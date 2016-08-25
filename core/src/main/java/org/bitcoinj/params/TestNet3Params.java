/*
 * Copyright 2013 Google Inc.
 * Copyright 2014 Andreas Schildbach
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

import java.math.BigInteger;
import java.util.Date;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Utils;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;

import static com.google.common.base.Preconditions.checkState;

/**
 * Parameters for the testnet, a separate public instance of Bitcoin that has relaxed rules suitable for development
 * and testing of applications and new Bitcoin versions.
 */
public class TestNet3Params extends AbstractBitcoinNetParams {
    public TestNet3Params() {
        super();
        id = ID_TESTNET;
        // Genesis hash is 0023e5f7be49f920756146f4bd0498f658f340ba41b0c8f2d2b83d1939703995
        packetMagic = 0x4555524f;
        interval = INTERVAL;
        targetTimespan = TARGET_TIMESPAN;
        port = 8989;
        addressHeader = 33;
        p2shHeader = 5;
        acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        dumpedPrivateKeyHeader = 168;
        genesisBlock.setTime(1468016542);
        genesisBlock.setDifficultyTarget(0x21000fff);
        genesisBlock.setNonce(1989891989);
        genesisBlock.setStartLocation(187905);
        genesisBlock.setFinalCaclucation(1629771148);
        spendableCoinbaseDepth = 100;
        subsidyDecreaseBlockCount = 819678;
        String genesisHash = genesisBlock.getHashAsString();
        checkState(genesisHash.equals("0023e5f7be49f920756146f4bd0498f658f340ba41b0c8f2d2b83d1939703995"),
            genesisHash);
        alertSigningKey = Utils.HEX.decode("045244948ceada9a43a7a5d5f6c56306575a7adfcc07754aec34eea88026d7e7f0acd9089edaac9aa734db5a4eb1aee96b9cedb8260c4ecff4f7db25a5952487aa");

        addrSeeds = new int[] { 0x4b44d655 };
        bip32HeaderPub = 0x043587CF;
        bip32HeaderPriv = 0x04358394;

        majorityEnforceBlockUpgrade = TestNet2Params.TESTNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = TestNet2Params.TESTNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = TestNet2Params.TESTNET_MAJORITY_WINDOW;
    }

    private static TestNet3Params instance;
    public static synchronized TestNet3Params get() {
        if (instance == null) {
            instance = new TestNet3Params();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return PAYMENT_PROTOCOL_ID_TESTNET;
    }
}
