var xhr = new XMLHttpRequest();
var url = 'http://api.data.go.kr/openapi/tn_pubr_public_traffic_light_api'; /*URL*/
var queryParams = '?' + encodeURIComponent('serviceKey') + '='+'서비스키'; /*Service Key*/
queryParams += '&' + encodeURIComponent('pageNo') + '=' + encodeURIComponent('0'); /**/
queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('100'); /**/
queryParams += '&' + encodeURIComponent('type') + '=' + encodeURIComponent('xml'); /**/
queryParams += '&' + encodeURIComponent('ctprvnNm') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('signguNm') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('roadKnd') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('roadRouteNo') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('roadRouteNm') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('roadRouteDrc') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('rdnmadr') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('lnmadr') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('latitude') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('longitude') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('sgngnrInstlMthd') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('roadType') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('priorRoadYn') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('tfclghtManageNo') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('tfclghtSe') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('tfclghtColorKnd') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('sgnaspMthd') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('sgnaspOrdr') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('sgnaspTime') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('sotKnd') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('signlCtrlMthd') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('signlTimeMthdType') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('opratnYn') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('flashingLightOpenHhmm') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('flashingLightCloseHhmm') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('fnctngSgngnrYn') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('remndrIdctYn') + '=' + encodeURIComponent(''); /**/
queryParams += '&' + encodeURIComponent('sondSgngnrYn') + '=' + encodeURIComponent(''); /**/

xhr.open('GET', url + queryParams);
xhr.onreadystatechange = function () {
    if (this.readyState == 4) {
        alert('Status: '+this.status+'nHeaders: '+JSON.stringify(this.getAllResponseHeaders())+'nBody: '+this.responseText);
    }
};

xhr.send('');