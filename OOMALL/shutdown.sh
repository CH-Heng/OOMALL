#!/bin/bash

target_dir=`pwd`

pid=`ps ax | grep -i ${1} | grep ${target_dir} | grep java | grep -v grep | awk '{print $SERVER}'`
if [ -z "$pid" ] ; then
        echo "No ${1} Server running."
        exit -1;
fi

echo "The Server(${pid}) is running..."

kill ${pid}

echo "Send shutdown request to Server(${pid}) OK"
