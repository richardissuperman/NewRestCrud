package com.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.backend.dao.DataDao;
import com.backend.model.Employee;

public class DataServicesImpl implements DataServices {

	@Autowired
	DataDao dataDao;
	
	@Override
	public boolean addEntity(Employee employee) throws Exception {
		return dataDao.addEntity(employee);
	}

	@Override
	public Employee getEntityById(long id) throws Exception {
		return dataDao.getEntityById(id);
	}

	@Override
	public List<Employee> getEntityList() throws Exception {
		return dataDao.getEntityList();
	}

	@Override
	public boolean deleteEntity(long id) throws Exception {
		return dataDao.deleteEntity(id);
	}

	@Override
	public boolean updateEntity(Employee employee) throws Exception {
		// TODO Auto-generated method stub
		return dataDao.updateEntity(employee);
	}

}
