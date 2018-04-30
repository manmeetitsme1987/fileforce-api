package fileforce.Mapper;

import fileforce.Model.Response.TestResponse;

public interface CommonMapper {
	
	//method to fetch the base 
	TestResponse getUserInfoById(int id);
}
