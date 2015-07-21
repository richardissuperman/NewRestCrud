package com.backend.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;

import com.backend.model.Employee;
import com.backend.model.Status;
import com.backend.services.DataServices;
import com.backend.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
@RequestMapping("/employee")
public class RestController {

	@Autowired
	DataServices dataServices;

	static final Logger logger = Logger.getLogger(RestController.class);

	// test link: http://localhost:8080/backend/api/task/getParams
	@RequestMapping(value = "/getParams", method = { RequestMethod.POST,
			RequestMethod.GET })
	public @ResponseBody
	void getParams(HttpServletRequest request) {

		Map<String, String[]> parameters = request.getParameterMap();
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		if (!StringUtil.isNullandEmpty(username)) {
			System.out.println(username);
		}
		if (!StringUtil.isNullandEmpty(password)) {
			System.out.println(password);
		}

		for (String key : parameters.keySet()) {
			// System.out.println(key);
			String[] vals = parameters.get(key);
			for (String val : vals) {
				if (!val.equals("")) {
					System.out.println(key + " -> " + val);
				} else {
					System.out.println(key + "-> is empty");
				}
			}
		}
	}

	@RequestMapping(value = "/uploadMyFile", method = RequestMethod.POST)
	@ResponseBody
	public void handleFileUpload(MultipartHttpServletRequest request)
			throws Exception {
		Iterator<String> itrator = request.getFileNames();
		MultipartFile multiFile = request.getFile(itrator.next());
		try {
			// just to show that we have actually received the file
			System.out.println("File Length:" + multiFile.getBytes().length);
			System.out.println("File Type:" + multiFile.getContentType());
			String fileName = multiFile.getOriginalFilename();
			System.out.println("File Name:" + fileName);
			String path = "c:\\";
			System.out.println(path);
			// making directories for our required path.
			byte[] bytes = multiFile.getBytes();
			File directory = new File(path + "/uploads");
			directory.mkdirs();
			// saving the file
			File file = new File(directory.getAbsolutePath()
					+ System.getProperty("file.separator") + fileName);
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(file));
			stream.write(bytes);
			stream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("Error while loading the file");
		}
		// return "File Uploaded successfully.";
	}

	@RequestMapping(value = "/read/{filename}", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadStuff(
			@PathVariable String filename) throws IOException {
		String[] sub = filename.split(".");
		System.out.println(filename);
		HttpHeaders respHeaders = new HttpHeaders();
		String fullPath = "";
		if (sub.length == 2) {
			fullPath = "c:\\uploads\\" + sub[0] + sub[1];
		}
		File file = null;
		try {
			file = new File(fullPath);
		} catch (Exception e) {
			return new ResponseEntity<InputStreamResource>(null, respHeaders,
					HttpStatus.NOT_FOUND);
		}

		if (file != null) {

			respHeaders.setContentType(new MediaType("application", "png"));
			respHeaders.setContentLength(file.length());

			InputStreamResource isr = new InputStreamResource(
					new FileInputStream(file));
			return new ResponseEntity<InputStreamResource>(isr, respHeaders,
					HttpStatus.OK);
		}

		return new ResponseEntity<InputStreamResource>(null, respHeaders,
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// create new employee
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Status addEmployee(@RequestBody Employee employee) {
		try {
			dataServices.addEntity(employee);
			return new Status(1, "Employee added Successfully !");
		} catch (Exception e) {
			// e.printStackTrace();
			return new Status(0, e.toString());
		}

	}

	// get the entity in DB by using id number
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody
	Employee getEmployee(@PathVariable("id") long id) {
		Employee employee = null;
		try {
			employee = dataServices.getEntityById(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return employee;
	}

	// list all the entities in DB
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody
	List<Employee> getEmployee() {

		List<Employee> employeeList = null;
		try {
			employeeList = dataServices.getEntityList();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return employeeList;
	}

	// delete the item by id
	@RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
	public @ResponseBody
	Status deleteEmployee(@PathVariable("id") long id) {

		try {
			dataServices.deleteEntity(id);
			return new Status(1, "Employee deleted Successfully !");
		} catch (ObjectNotFoundException e) {

			return new Status(0, "Object is not found!");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}

	}
	
	
	// update the item by id
	@RequestMapping(value = "update/{id}", method = RequestMethod.POST,  consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Status updateEmployee(@RequestBody Employee employee) {

		try {
			dataServices.updateEntity(employee);
			return new Status(1, "Employee updated Successfully !");
		} catch (ObjectNotFoundException e) {

			return new Status(0, "Object is not found!");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}

	}
	
	
	
	
	// update the item by id
	@RequestMapping(value = "updatebyparameters/{id}", method = RequestMethod.POST)
	public @ResponseBody
	Status updateEmployee(HttpServletRequest request,@PathVariable("id") long id) {

		Map<String, String[]> parameters = request.getParameterMap();
	 
		String username = request.getParameter("username");
		String lastname = request.getParameter("lastname");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		
		System.out.println("id is: "+id);
 
		Employee em=new Employee();
		em.setId(id);
		em.setFirstName(username);
		em.setLastName(lastname);
		em.setEmail(email);
		em.setPhone(phone);
 
		try {
			dataServices.updateEntity(em);
			return new Status(1, "Employee updated from Parameters Successfully !");
		} catch (ObjectNotFoundException e) {

			return new Status(0, "Object is not found!");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}

	}
	
}
