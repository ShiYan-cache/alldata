package com.platform.rpc.remoting.invoker.route.impl;

import com.platform.rpc.remoting.invoker.route.XxlRpcLoadBalance;

import java.util.Random;
import java.util.TreeSet;

/**
 * random
 *
 * @author AllDataDC 2023-01-04
 */
public class XxlRpcLoadBalanceRandomStrategy extends XxlRpcLoadBalance {

    private Random random = new Random();

    @Override
    public String route(String serviceKey, TreeSet<String> addressSet) {
        // arr
        String[] addressArr = addressSet.toArray(new String[addressSet.size()]);

        // random
        String finalAddress = addressArr[random.nextInt(addressSet.size())];
        return finalAddress;
    }

}
