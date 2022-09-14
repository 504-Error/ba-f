checkStorage();
function checkStorage(){
    let storageData = JSON.parse(window.sessionStorage.getItem('messageForm'));

    if(isNull(storageData)) { // localstroage 비어 있으면 현재 위치 파악해서 출발지 입력 칸 채우기
        currentLocationCoord();
    } else { // local storage에 값 있으면 해당 데이터로  입력 폼 채우기
        let passengerName = document.getElementById("passengerName"),
            sourceAddress = document.getElementById("sourceAddress"),
            destinationAddress = document.getElementById("destinationAddress"),
            passengerNum = document.getElementById("passengerNum");

        passengerName.setAttribute('value',storageData.passengerName);
        sourceAddress.setAttribute('value',storageData.sourceAddress);
        destinationAddress.setAttribute('value',storageData.destinationAddress);
        passengerNum.value = storageData.passengerNum;
        let radioBtn = document.getElementById(storageData.wChairRadioBtn);
        radioBtn.checked = true;

        sessionStorage.clear();
    }
}

// 현재 위치 좌표 얻어오는 함수
function currentLocationCoord() {

    // HTML5의 geolocation으로 사용할 수 있는지 확인합니다
    if (navigator.geolocation) {

        // GeoLocation을 이용해서 접속 위치를 얻어옵니다
        navigator.geolocation.getCurrentPosition(function(position) {

            var lat = position.coords.latitude, // 위도
                lon = position.coords.longitude; // 경도

            var locPosition = new kakao.maps.LatLng(lat, lon); // 마커가 표시될 위치를 geolocation으로 얻어온 좌표로 생성합니다

            getDetailAddrFromCoords(locPosition);

        });
    } else { // GeoLocation을 사용할 수 없는 경우. 빈칸(주소를 입력해주세요)
        let addrInputField = document.getElementById('sourceAddress');
        addrInputField.setAttribute('placeholder', "출발지를 입력해주세요");

    }
    return true
}

// 현재 좌표를 주소로 바꾸는 함수
function getDetailAddrFromCoords(locPosition) {
    let geocoder = new kakao.maps.services.Geocoder();
    let resultAddr;

    let callback = function(result, status) {
        if (status === kakao.maps.services.Status.OK) {
            // console.log(result);
            if(result[0].road_address != null){
                resultAddr = result[0].road_address.address_name;
            }else{
                resultAddr = result[0].address.address_name;
            }

            let addrInputField = document.getElementById('sourceAddress');
            addrInputField.setAttribute('value',resultAddr);
        }
    }
    geocoder.coord2Address(locPosition.getLng(), locPosition.getLat(), callback);
}
// 출발지 검색 -> 입력 폼 내용 localStroage에 저장
// document.getElementById("desBtn").addEventListener('click',onClickDestinationBtn);
function onClickSourceBtn(){
    let passengerName = document.getElementById("passengerName").value,
        sourceAddress = document.getElementById("sourceAddress").value,
        destinationAddress = document.getElementById("destinationAddress").value,
        passengerNum = document.getElementById("passengerNum").value,
        wChairRadioBtn =  $('input[name=wChairRadioBtn]:checked').val();
    let obj = {"passengerName": passengerName, "sourceAddress": sourceAddress, "destinationAddress": destinationAddress, "passengerNum": passengerNum, "wChairRadioBtn": wChairRadioBtn}
    let objStr = JSON.stringify(obj);
    window.sessionStorage.setItem('messageForm', objStr);

    console.log(window.sessionStorage.getItem('messageForm'));
}

// 도착지 검색 -> 입력 폼 내용 localStroage에 저장
// document.getElementById("desBtn").addEventListener('click',onClickDestinationBtn);
function onClickDestinationBtn(){
    let passengerName = document.getElementById("passengerName").value,
        sourceAddress = document.getElementById("sourceAddress").value,
        destinationAddress = document.getElementById("destinationAddress").value,
        passengerNum = document.getElementById("passengerNum").value,
        wChairRadioBtn =  $('input[name=wChairRadioBtn]:checked').val();
    let obj = {"passengerName": passengerName, "sourceAddress": sourceAddress, "destinationAddress": destinationAddress, "passengerNum": passengerNum, "wChairRadioBtn": wChairRadioBtn}
    let objStr = JSON.stringify(obj);
    window.sessionStorage.setItem('messageForm', objStr);

    console.log(window.sessionStorage.getItem('messageForm'));
}


// 입력폼에 작성한 내용을 기반으로 문자 작성
// document.getElementById("writeBtn").addEventListener('click',onClickWriteBtn);
function onClickWriteBtn(){
    // 입력값 확인 -> 문자 작성
    let passengerName = document.getElementById("passengerName").value,
        sourceAddress = document.getElementById("sourceAddress").value,
        destinationAddress = document.getElementById("destinationAddress").value,
        passengerNum = document.getElementById("passengerNum").value,
        wChairRadioBtn =  $('input[name=wChairRadioBtn]');

    if(!isNull(passengerName) && !isNull(sourceAddress) && !isNull(destinationAddress) && !isNull(passengerNum) && wChairRadioBtn.is(":checked")){
        let msgStr = passengerName + " 장애인 콜택시 접수합니다. \n" +
            "출발지 : " + sourceAddress + "\n" +
            "도착지 : " + destinationAddress + "\n" +
            "탑승인원 : " + passengerNum;

        if($('input[name=wChairRadioBtn]:checked').val() == "wChairYes"){
            msgStr += "\n" + "휠체어 사용";
        }

        let msgBox = document.getElementById("msgTextarea");
        msgBox.innerHTML = msgStr;
    } else {
        console.log(!isNull(passengerName));
        console.log(!isNull(sourceAddress));
        console.log(!isNull(destinationAddress));
        console.log(!isNull(passengerNum));
        console.log(wChairRadioBtn.is(":checked"));
        alert("필수 내용 입력을 확인하세요.");
    }
}

// 입력값 확인
function isNull(v) {
    return (v === undefined || v === null || v === "") ? true : false;
}

// '문자 전송' 버튼 클릭 -> QR 코드 생성
function onClickSendBtn(){
    let msgStr = document.getElementById("msgTextarea").value;
    let qrUrl = "https://api.scanova.io/v2/qrcode/SMS?phone_no=15884388&apikey=dajucztaprhlugmnpnglgmghgixrnxrzeddsxnxo&size=s&message=" + msgStr;
    $("#QRImage").attr("src", qrUrl);
    $("#QRImage").attr("style", "width: 250px; height: 250px;");
}
