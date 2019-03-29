app.controller("indexController",function($scope,loginService){
	
	$scope.showName = function(){
		loginService.showName().success(function(response){
			$scope.loginName = response.username;
		});
	}

	//当前时间
	$scope.data = new Date().toLocaleDateString();
	$scope.time = new Date().toLocaleTimeString();
});