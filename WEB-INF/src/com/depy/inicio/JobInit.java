package com.depy.inicio;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppCleanup;
import org.zkoss.zk.ui.util.WebAppInit;

import com.depy.job.ConsultarEstadosDE;
import com.depy.job.ConsultarEstadosEventos;
import com.doxacore.util.SystemInfo;


public class JobInit implements WebAppInit, WebAppCleanup{

	  @Override
	  public void init(WebApp webApp) throws Exception {
		  

	        //System.out.println("Iniciando Quartz en "+SystemInfo.SISTEMA_PATH_NOMBRE+" ...");

	        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

	        // === Trigger: cada 10 segundos ===
	      /*  Trigger trigger = TriggerBuilder.newTrigger()
	                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
	                        .withIntervalInSeconds(10)
	                        .repeatForever())
	                .build();*/

	        // === O Trigger con CRON ===
	        Trigger trigger = TriggerBuilder.newTrigger()
	                 .withSchedule(CronScheduleBuilder.cronSchedule("0 25,55 * * * ?"))
	                 .build();

	        JobDetail job = JobBuilder.newJob(ConsultarEstadosDE.class)
	                .withIdentity("ConsultaEstados", "group"+SystemInfo.SISTEMA_PATH_NOMBRE)
	                .build();
	        
	        scheduler.scheduleJob(job, trigger);
	        
	        Trigger trigger2 = TriggerBuilder.newTrigger()
	                 .withSchedule(CronScheduleBuilder.cronSchedule("0 17,47 * * * ?"))
	                 .build();

	        JobDetail job2 = JobBuilder.newJob(ConsultarEstadosEventos.class)
	                .withIdentity("ConsultaEvento", "group"+SystemInfo.SISTEMA_PATH_NOMBRE)
	                .build();
	        
	        scheduler.scheduleJob(job2, trigger2);
	        
	        scheduler.start();
	       
	        webApp.setAttribute("scheduler", scheduler);
	        
	    }

	  @Override
	  public void cleanup(WebApp webApp) throws Exception {

	        Scheduler scheduler = (Scheduler) webApp.getAttribute("scheduler");
	        if (scheduler != null) {
	            scheduler.shutdown(true); // espera que terminen los jobs en ejecución
	            System.out.println("Quartz detenido correctamente al cerrar WebApp");
	        }
	    }
	
}
