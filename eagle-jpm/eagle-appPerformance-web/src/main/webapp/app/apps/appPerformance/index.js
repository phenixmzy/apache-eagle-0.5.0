/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

(function () {
	/**
	 * `register` is global function that for application to set up 'controller', 'service', 'directive', 'route' in Eagle
	 */
	var appPerformance = register(['ngRoute', 'ngAnimate', 'ui.router', 'eagle.service']);

	appPerformance.route("Elephant", {
	    url: "/appPerformance/elephant",
        site: true,
        templateUrl: "partials/overview.html",
        controller: "elephantCtrl"
    });

	appPerformance.portal(
	    {name: "Application Performance", icon: "taxi", list: [
		{name: "Dr.Elephant", path: "appPerformance/elephant"}
	]}, true);

	appPerformance.service("Application Performance", function ($q, $http, $scope, Time, Site, Application) {
		var DR = window._DR = {};
		DR.ELEPHANT = '${baseURL}/rest/appPerformance/elephant';
		$scope.elephantUrl = "www.163.com";
		/**
		 * Fetch elephant web address content with current site application configuration
		 */
		/*
		var elephant = DR.elephant = function(siteId) {
			var baseURL;
			var elephantURL;
			siteId = siteId || Site.current().siteId;
			var app = Application.find("DrElephant_WEB_APP", siteId)[0];
			var elephantUrl = app.configuration["service.elephantUrl"];
			var host = app.configuration["service.host"];
			var port = app.configuration["service.port"];

			if(!host && !port) {
				baseURL = "";
			} else {
				if(host === "localhost" || !host) {
					host = location.hostname;
				}
				if(!port) {
					port = location.port;
				}
				baseURL = "http://" + host + ":" + port;
			}

			if(!elephantUrl) {
				elephantURL =  app.configuration["drelephant.url"];
			} else {
				if(elephantUrl === "localhost" || !elephantUrl) {
					elephantURL = elephantUrl
				}
			}
			return elephantURL;
		};

		DR.get = function (url) {
			return $http({
				url: url,
				method: "GET"
			});
		};
		*/

		return DR;
	});
	appPerformance.requireCSS("style/index.css");
	appPerformance.require("ctrl/elephantCtrl.js");
})();
