package com.kh.healthDao.admin.model.service;

import java.util.Map;

import com.kh.healthDao.admin.model.vo.Notice;

public interface NoticeService {

	int noticeInsert(Notice notice);

	Map<String, Object> allNoticeList(int page);

	Notice noticeDetail(int nNo);

	int noticeModify(Notice notice);
	
}
