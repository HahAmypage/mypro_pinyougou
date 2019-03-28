app.controller("contentController",function($scope,contentService){
	$scope.contentList = [];
	// 根据分类ID查询广告的方法:
	$scope.findByCategoryId = function(categoryId){
		contentService.findByCategoryId(categoryId).success(function(response){
			$scope.contentList[categoryId] = response;
		});
	}
	
	//搜索  （传递参数）
	$scope.search=function(){
		//注意这里的端口是自己portal的
		location.href="http://localhost:23004/search.html#?keywords="+$scope.keywords;
	}
	
});