package com.depy.sistemaResp.main;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

public class MainRespVM {
	
	private String currentUriPage = "/sistema-resp/zul/main/main.zul";
	
	@Command
	@NotifyChange("currentUriPage")
	public void navegar(@BindingParam("uriPage") String uriPage) {
		
		this.currentUriPage = uriPage;
		Clients.evalJavaScript("closeDrawer()");
		
	}

	public String getCurrentUriPage() {
		return currentUriPage;
	}

	public void setCurrentUriPage(String currentUriPage) {
		this.currentUriPage = currentUriPage;
	}
	
	
	
}
