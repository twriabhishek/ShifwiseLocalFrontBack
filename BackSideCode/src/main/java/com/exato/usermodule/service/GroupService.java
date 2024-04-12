package com.exato.usermodule.service;

import java.util.List;
import com.exato.usermodule.model.GroupModel;

import jakarta.servlet.http.HttpServletRequest;

public interface GroupService {

	GroupModel createGroup(GroupModel groupModel, HttpServletRequest request);

	List<GroupModel> getAllGroups(HttpServletRequest request);

	GroupModel getGroupById(Long id);

	GroupModel updateGroup(Long id, GroupModel groupModel, HttpServletRequest request);

	void deleteGroup(Long id, HttpServletRequest request);
}
