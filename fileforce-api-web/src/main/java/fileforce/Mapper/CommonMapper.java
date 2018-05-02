package fileforce.Mapper;

import fileforce.Model.Response.MasterTableResponse;
import fileforce.Model.Response.TestResponse;

public interface CommonMapper {
	
	//method to fetch the base 
	TestResponse getUserInfoById(int id);
	
	//fetching master table data
	MasterTableResponse getMasterData(String orgId);
}
