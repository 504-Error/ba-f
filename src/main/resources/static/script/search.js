// 마커를 클릭했을 때 해당 장소의 상세정보를 보여줄 커스텀오버레이입니다
var placeOverlay = new kakao.maps.CustomOverlay({zIndex:1}),
    contentNode = document.createElement('div'), // 커스텀 오버레이의 컨텐츠 엘리먼트 입니다
    markers = []; // 마커를 담을 배열입니다

var mapContainer = document.getElementById('map'), // 지도를 표시할 div
    mapOption = {
        center: new kakao.maps.LatLng(37.566826, 126.9786567), // 지도의 중심좌표
        level: 3 // 지도의 확대 레벨
    };

// 지도를 생성합니다
var map = new kakao.maps.Map(mapContainer, mapOption);

// 장소 검색 객체를 생성합니다
var ps = new kakao.maps.services.Places();

// 커스텀 오버레이의 컨텐츠 노드에 css class를 추가합니다
contentNode.className = 'placeinfo_wrap';

// 커스텀 오버레이의 컨텐츠 노드에 mousedown, touchstart 이벤트가 발생했을때
// 지도 객체에 이벤트가 전달되지 않도록 이벤트 핸들러로 kakao.maps.event.preventMap 메소드를 등록합니다
addEventHandle(contentNode, 'mousedown', kakao.maps.event.preventMap);
addEventHandle(contentNode, 'touchstart', kakao.maps.event.preventMap);

// 커스텀 오버레이 컨텐츠를 설정합니다
placeOverlay.setContent(contentNode);

// 엘리먼트에 이벤트 핸들러를 등록하는 함수입니다
function addEventHandle(target, type, callback) {
    if (target.addEventListener) {
        target.addEventListener(type, callback);
    } else {
        target.attachEvent('on' + type, callback);
    }
}


// 키워드 검색을 요청하는 함수입니다
function searchPlaces() {

    var keyword = document.getElementById('keyword').value;

    if (!keyword.replace(/^\s+|\s+$/g, '')) {
        alert('키워드를 입력해주세요!');
        return false;
    }

    // 장소검색 객체를 통해 키워드로 장소검색을 요청합니다
    ps.keywordSearch( keyword, placesSearchCB);
}

// 장소검색이 완료됐을 때 호출되는 콜백함수 입니다
function placesSearchCB(data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {

        // 정상적으로 검색이 완료됐으면
        // 검색 목록과 마커를 표출합니다
        displayPlaces(data);

        // 페이지 번호를 표출합니다
        displayPagination(pagination);

    } else if (status === kakao.maps.services.Status.ZERO_RESULT) {

        alert('검색 결과가 존재하지 않습니다.');
        return;

    } else if (status === kakao.maps.services.Status.ERROR) {

        alert('검색 결과 중 오류가 발생했습니다.');
        return;

    }
}

// 검색 결과 목록과 마커를 표출하는 함수입니다
function displayPlaces(places) {

    var listEl = document.getElementById('placesList'),
        menuEl = document.getElementById('menu_wrap'),
        fragment = document.createDocumentFragment(),
        bounds = new kakao.maps.LatLngBounds(),
        listStr = '';

    // 검색 결과 목록에 추가된 항목들을 제거합니다
    removeAllChildNods(listEl);

    // 지도에 표시되고 있는 마커를 제거합니다
    removeMarker();

    for ( var i=0; i<places.length; i++ ) {

        // 마커를 생성하고 지도에 표시합니다
        var placePosition = new kakao.maps.LatLng(places[i].y, places[i].x),
            marker = addMarker(placePosition, i),
            itemEl = getListItem(i, places[i]); // 검색 결과 항목 Element를 생성합니다

        (function(marker, place) {
            kakao.maps.event.addListener(marker, 'click', function() {
                displayPlaceInfo(place);
            });
            itemEl.onclick =  function () {
                panTo(place.y, place.x);
                storeInfo(place);
                console.log("click");
            };
        })(marker, places[i]);

        // 검색된 장소 위치를 기준으로 지도 범위를 재설정하기위해
        // LatLngBounds 객체에 좌표를 추가합니다
        bounds.extend(placePosition);
        fragment.appendChild(itemEl)


    }

    // 검색결과 항목들을 검색결과 목록 Element에 추가합니다
    listEl.appendChild(fragment);
    menuEl.scrollTop = 0;


    // 검색된 장소 위치를 기준으로 지도 범위를 재설정합니다
    map.setBounds(bounds);
}

// 검색결과 항목을 Element로 반환하는 함수입니다
function getListItem(index, places) {

    var el = document.createElement('li'),
        itemStr = '<span class="markerbg marker_' + (index+1) + '"></span>' +
            '<div class="info">' +
            '   <h5>' + places.place_name + '</h5>';

    if (places.road_address_name) {
        itemStr += '    <span>' + places.road_address_name + '</span>' +
            '   <span class="jibun gray">' +  places.address_name  + '</span>';
    } else {
        itemStr += '    <span>' +  places.address_name  + '</span>';
    }

    itemStr += '  <span class="tel">' + places.phone + '</span>' ;

    itemStr += '  <a class="review" href="$(/review/content/7)">' + "리뷰 보러가기" + '</a>' +
        '</div>';
    el.innerHTML = itemStr;
    el.className = 'item';
    return el;
}

// 마커를 생성하고 지도 위에 마커를 표시하는 함수입니다
function addMarker(position, idx, title) {
    var imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', // 마커 이미지 url, 스프라이트 이미지를 씁니다
        imageSize = new kakao.maps.Size(36, 37),  // 마커 이미지의 크기
        imgOptions =  {
            spriteSize : new kakao.maps.Size(36, 691), // 스프라이트 이미지의 크기
            spriteOrigin : new kakao.maps.Point(0, (idx*46)+10), // 스프라이트 이미지 중 사용할 영역의 좌상단 좌표
            offset: new kakao.maps.Point(13, 37) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
        },
        markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions),
        marker = new kakao.maps.Marker({
            position: position, // 마커의 위치
            image: markerImage
        });

    marker.setMap(map); // 지도 위에 마커를 표출합니다
    markers.push(marker);  // 배열에 생성된 마커를 추가합니다

    return marker;
}
//목록을 선택하면 상세정보 창을 보여주는 함수입니다.
function storeInfo(place){
    let elWrap = document.getElementById('map_wrapper'),
        info = document.createElement('div'),
        expBefore = document.getElementById('menu_info_wrap');

    if(expBefore != null){
        expBefore.remove();
    }

    let str = '<div class="wrap">'+
    '<iframe class="frame" src="'+ place.place_url +'" id = "chat_iframe"></iframe>'+
    '</div>'+
    '<ul class="list_evaluation">\n' +
        '        <li>\n' +
        '            <div class="unit_info">\n' +
        '                <em class="screen_out">작성자 : </em>\n' +
        '                <a href="#none" class="link_user">어쩌구</a>\n' +
        '                <span class="bg_bar"></span>\n' +
        '                <span class="screen_out">글 작성일 : </span>\n' +
        '                <span class="time_write">2022.01.01</span>\n' +
        '            </div>\n' +
        '            <div class="star_info">\n' +
        '                <div class="grade_star size_s">\n' +
        '\t\t\t\t      <span class="ico_star star_rate">\n' +
        '\t\t\t\t        <span class="ico_star inner_star" style="width:100%;"></span>\n' +
        '\t\t\t\t      </span>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="comment_info">\n' +
        '                <p class="txt_comment"><span>리뷰 텍스트</span>\n' +
        '                    <button type="button" class="btn_fold">더보기</button></p>\n' +
        '            </div>\n' +
        '            <div class="group_photo">\n' +
        '                <strong class="screen_out">편의사항 이미지</strong>\n' +
        '                <ul class="list_photo">\n' +
        '                    <li>\n' +
        '                        <a href="#none" data-basis="5742999" data-pidx="0" data-type="kakaomapcomment">\n' +
        '                            <img src="/static/image/amenities_elevator_select.png" width="80" height="80" alt="후기사진">\n' +
        '                        </a>\n' +
        '                    </li>\n' +
        '                    <li>\n' +
        '                        <a href="#none" data-basis="5742999" data-pidx="1" data-type="kakaomapcomment">\n' +
        '                            <img src="/static/image/amenities_incline_select.png" width="80" height="80" alt="후기사진">\n' +
        '                        </a>\n' +
        '                    </li>\n' +
        '                    <li>\n' +
        '                        <a href="#none" data-basis="5742999" data-pidx="2" data-type="kakaomapcomment">\n' +
        '                            <img src="/static/image/amenities_parking_select.png" width="100" height="100" alt="후기사진">\n' +
        '                        </a>\n' +
        '                    </li>\n' +
        '                    <li>\n' +
        '                        <a href="#none" data-basis="5742999" data-pidx="3" data-type="kakaomapcomment">\n' +
        '                            <img src="/static/image/amenities_rest-room_select.png" width="80" height="80" alt="후기사진">\n' +
        '                        </a>\n' +
        '                    </li>\n' +
        '                    <li>\n' +
        '                        <a href="#none" data-basis="5742999" data-pidx="4" data-type="kakaomapcomment">\n' +
        '                            <img src="/static/image/amenities_table_select.png" width="80" height="80" alt="후기사진">\n' +
        '                        </a>\n' +
        '                    </li>\n' +
        '                </ul>\n' +
        '            </div>\n' +
        '        </li>\n' +
        '    </ul>'

    info.setAttribute("id", "menu_info_wrap");
    info.className='bg_white';
    info.innerHTML = str;

    console.log(info);

    elWrap.appendChild(info);
}

// 지도 위에 표시되고 있는 마커를 모두 제거합니다
function removeMarker() {
    for ( var i = 0; i < markers.length; i++ ) {
        markers[i].setMap(null);
    }
    markers = [];
}

// 클릭한 마커에 대한 장소 상세정보를 커스텀 오버레이로 표시하는 함수입니다
function displayPlaceInfo (place) {
    var content = '<div class="placeinfo">' +
        '   <a class="title" href="' + place.place_url + '" target="_blank" title="' + place.place_name + '">' + place.place_name + '</a>';

    if (place.road_address_name) {
        content += '    <span title="' + place.road_address_name + '">' + place.road_address_name + '</span>' +
            '  <span class="jibun" title="' + place.address_name + '">(지번 : ' + place.address_name + ')</span>';
    }  else {
        content += '    <span title="' + place.address_name + '">' + place.address_name + '</span>';
    }

    content += '    <span class="tel">' + place.phone + '</span>' +
        '</div>' +
        '<div class="after"></div>';
    panTo(place.y,place.x);
    contentNode.innerHTML = content;
    placeOverlay.setPosition(new kakao.maps.LatLng(place.y, place.x));
    placeOverlay.setMap(map);
}

function panTo(y,x) {
    // 이동할 위도 경도 위치를 생성합니다
    var moveLatLon = new kakao.maps.LatLng(y,x);

    // 지도 중심을 부드럽게 이동시킵니다
    // 만약 이동할 거리가 지도 화면보다 크면 부드러운 효과 없이 이동합니다
    map.panTo(moveLatLon);
    map.setLevel(2);
    map.panTo(moveLatLon);
}

// 검색결과 목록 하단에 페이지번호를 표시는 함수입니다
function displayPagination(pagination) {
    var paginationEl = document.getElementById('pagination'),
        fragment = document.createDocumentFragment(),
        i;

    // 기존에 추가된 페이지번호를 삭제합니다
    while (paginationEl.hasChildNodes()) {
        paginationEl.removeChild (paginationEl.lastChild);
    }

    for (i=1; i<=pagination.last; i++) {
        var el = document.createElement('a');
        el.href = "#";
        el.innerHTML = i;

        if (i===pagination.current) {
            el.className = 'on';
        } else {
            el.onclick = (function(i) {
                return function() {
                    pagination.gotoPage(i);
                }
            })(i);
        }

        fragment.appendChild(el);
    }
    paginationEl.appendChild(fragment);
}


// 검색결과 목록의 자식 Element를 제거하는 함수입니다
function removeAllChildNods(el) {
    while (el.hasChildNodes()) {
        el.removeChild (el.lastChild);
    }
}