package com.yihaodian.ydd.monitor.aspectj;

import com.yihaodian.ydd.monitor.data.optrecdata.ResultSetOptRecData.CloseResultSet;
import com.yihaodian.ydd.monitor.processor.MonitorDataProcessor;
import com.yihaodian.ydd.monitor.proxy.ResultSetMonitorProxy;
import java.util.Properties;
import java.lang.Boolean;

public aspect ResultSetMonitorAspectJ {

    after(ResultSetMonitorProxy rsmp) : execution(void com.yihaodian.ydd.monitor.proxy.ResultSetMonitorProxy.close())
        && target(rsmp) && within(com.yihaodian.ydd.monitor.proxy.ResultSetMonitorProxy){
        MonitorDataProcessor processor = rsmp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean ssdSwitch = false;
        if(switchPro!=null){
         ssdSwitch = (Boolean)switchPro.get("ssdSwitch");
        }
        if(ssdSwitch){
        processor.putOperationRecord(new CloseResultSet(processor, rsmp.getExecuteSqlTime(), rsmp.getSql(),
                rsmp.getRetrieveRowCount().intValue()));
                }
    }
    }
}
