package fileforce.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fileforce.Mapper.CommonMapper;
import fileforce.Model.Response.TestResponse;

@Service
public class CommonService {
	@Autowired
	private CommonMapper commonMapper;
	
	public TestResponse getUserInfoById(int id) {
		TestResponse response = commonMapper.getUserInfoById(id);
		return response;
	}
}
