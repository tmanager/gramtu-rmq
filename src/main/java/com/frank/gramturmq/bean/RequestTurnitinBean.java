package com.frank.gramturmq.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 传参类.
 * 
 * @author 张孝党 2019/09/10.
 * 
 * @version V1.00.
 * 
 *          更新履历： V1.00 2019/09/10 张孝党 创建.
 */
@Getter
@Setter
public class RequestTurnitinBean {

	// 用户名
	private String uname = "";

	// 密码
	private String passwd = "";

	// classid
	private String classid = "";

	private String aid = "";

	private String subAid = "";

	// 姓
	private String firstName = "";

	// 名
	private String lastName = "";

	// 论文标题
	private String title = "";

	// 论文名称
	private String thesisName = "";

	// 论文ID
	private String thesisId = "";

	// 论文在A电脑上的本地路径
	private String thesisAPath = "";

	// 论文在B电脑上的本地路径
	private String thesisVpnPath = "";

	// 论文在B电脑上FTP路径
	private String thesisVpnFtpPath = "";

	// 报告文件名称
	private String reportFileName = "";

	// 报告在A电脑上的路径
	private String reportAPath = "";

	// 报告在B电脑上的保存路径
	private String reportVpnPath = "";

	// 报告在B电脑上的FTP路径
	private String reportVpnFtpPath = "";

	// FDFS论文路径
	private String originalurl = "";
}
