package com.depy.job;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.depy.util.UtilLocalMetodos;
import com.doxacore.util.Register;




public class ConsultarEstadosDE implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
	
		System.out.println("Iniciando consulta automatica de Facturas...");
		UtilLocalMetodos ulm = new UtilLocalMetodos();
		ulm.updateDE(new Register(), ulm.getSql("factura/listaFacturaPendiente.sql"), ulm.getSql("factura/updateFactura.sql"), "factura");
		
		System.out.println("Iniciando consulta automatica de Notas de Credito...");
		ulm = new UtilLocalMetodos();
		ulm.updateDE(new Register(), ulm.getSql("notacredito/listaNotaCreditoPendiente.sql"), ulm.getSql("notacredito/updateNotaCredito.sql"), "factura");
		
	}

}
