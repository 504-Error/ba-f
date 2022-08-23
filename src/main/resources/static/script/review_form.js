const select = document.getElementById("inputGenre");
const searchBtn = document.getElementById("btn-search");
// sessionStorage.clear();
if (sessionStorage.getItem('form_data')) {
    let form_data = JSON.parse(sessionStorage.getItem("form_data"));
    console.log("form data : ", form_data);
    const inputTitle = document.getElementById("inputTitle");
    const inputDate = document.getElementById("inputDate");
    const inputPlace = document.getElementById("inputPlace");
    const inputPlaceReview = document.getElementById("inputPlaceReview");
    const inputAdditionalReview = document.getElementById("inputAdditionalReview");

    if (form_data["genre"]) {
        $("#inputGenre").val(form_data["genre"]).prop("selected",true);
        if(form_data["genre"] === "음식점" || form_data["genre"] === "카페"){
            searchBtn.innerText = form_data["genre"] + " 검색하기";
        } else if(form_data["genre"] === "뮤지컬" || form_data["genre"] === "연극" || form_data["genre"] === "기타 공연"){
            searchBtn.innerText = "공연 검색하기";
        } else {
            searchBtn.innerText = "검색하기";
        }
    }

    inputTitle.value = form_data["subject"];
    inputDate.value = form_data["date"];
    inputPlace.value = form_data["place"];
    inputPlaceReview.value = form_data["placeReview"];
    inputAdditionalReview.value = form_data["additionalReview"];

    if (form_data["grade"] !== 0){
        document.getElementById(form_data["grade"] + "-stars-btn").checked = true;
    }

    const amenities_category = ["elevator", "incline", "parking", "table", "rest-room"];
    for (let i = 0; i < form_data["amenities"].length; i++) {
        isChecked(amenities_category[form_data["amenities"][i]]);
        document.getElementById("amenities-" + amenities_category[form_data["amenities"][i]]).checked = true;
    }

    if (form_data["isAnonymous"]){
        console.log("isAnonymous : ", form_data["isAnonymous"]);
        $("#check_anonymous").prop('checked',true);
    }
}

if (sessionStorage.getItem('selectPlaceData')) {
    const selectPlace = JSON.parse(sessionStorage.getItem('selectPlaceData'));
    console.log("selectPlaceData : ", selectPlace);
    const inputTitle = document.getElementById("inputTitle");
    const inputPlace = document.getElementById("inputPlace");

    inputTitle.value = selectPlace["placeName"];
    inputPlace.value = selectPlace["placeName"] + " (" + selectPlace["address"] + ")";

} else if (sessionStorage.getItem('selectPerformData')) {
    const selectPerform = JSON.parse(sessionStorage.getItem('selectPerformData'));
    console.log("selectPerformData : ", selectPerform);
    const inputTitle = document.getElementById("inputTitle");
    const inputPlace = document.getElementById("inputPlace");

    inputTitle.value = selectPerform["title"];
    inputPlace.value = selectPerform["place"] + " (" + selectPerform["address"] + ")";
}

if (sessionStorage.getItem('selectAddressData')) {
    const selectAddressData = JSON.parse(sessionStorage.getItem('selectAddressData'));
    console.log("selectAddressData : ", selectAddressData);
    const inputTitle = document.getElementById("inputTitle");
    const inputPlace = document.getElementById("inputPlace");

    if (inputTitle.value === "") {
        inputTitle.value = selectAddressData["placeName"];
    }
    inputPlace.value = selectAddressData["placeName"] + " (" + selectAddressData["address"] + ")";
}

function isChecked(category) {
    const imgId = "img_amenities_" + category
    const checkbox_id = "amenities-" + category;
    const checkbox = document.getElementById(checkbox_id)
    const isChecked = checkbox.checked;
    var changeImgName;
    if (!isChecked){
        changeImgName = "/static/image/amenities_" + category + "_select.png";
    } else {
        changeImgName = "/static/image/amenities_" + category + ".png";
    }

    document.getElementById(imgId).src = changeImgName;
}

var inputFileList = new Array();
function inputImgFile() {
    // 업로드 된 파일 유효성 체크
    const fileInput = document.getElementById("formFileMultiple");
    const selectedFile = fileInput.files;

    if ((inputFileList.length + selectedFile.length) > 5) {
        alert("이미지는 최대 5개까지 업로드 가능합니다.");
        $('input[name=images]').val();
        return;
    }

    addImg(selectedFile)
    console.log("selectedFile : ", selectedFile);
}

// 다중 이미지 미리보기 업로드
function addImg(selectedFile) {
    const fileInput = document.getElementById("formFileMultiple");
    console.log("addimg-selectedFile : ", selectedFile);
    for (let i = 0; i < selectedFile.length; i++){
        let div = document.createElement('div');
        div.className = "btn preview-img"
        div.id = (selectedFile[i].name).replace(/-/g, "_") + "-" + selectedFile[i].lastModified;
        div.onclick = function () {
            for(let i = 0; i < inputFileList.length; i++) {
                var fileData = div.id.split('-');
                console.log("div.id.split : ", div.id);
                console.log("fileData : ", fileData);
                if(inputFileList[i].name.replace(/-/g, "_") === fileData[0] && inputFileList[i].lastModified == fileData[1]) {
                    inputFileList.splice(i, 1);
                    console.log("delete img num : ", i);
                    break;
                }
            }
            document.getElementById('board-preview-add-img').removeChild(div);
            console.log("delete after inputFileList : ", inputFileList);
        }

        let child_div = document.createElement('div');
        child_div.className = "preview-img-background";
        div.appendChild(child_div);

        let p = document.createElement('p');
        p.className = "preview-img-del";
        p.appendChild(document.createTextNode("이미지 삭제"));
        child_div.appendChild(p);

        const fileReader = new FileReader();
        fileReader.onload = e => {
            div.style = "background-image:url(" + e.target.result + ");"
            document.getElementById('board-preview-add-img').appendChild(div);
        }

        fileReader.readAsDataURL(selectedFile[i]);
        inputFileList.push(selectedFile[i]);
        console.log("add img : ", selectedFile[i]);
    }

    fileInput.value = "";
}

$("#check_anonymous").change(function() {
    console.log("change check");
    if(this.checked) {
        $(this).attr('value', true);
    }
    else
    {
        $(this).attr('value', false);
    }
});

function changeGenre() {
    console.log("change inputGenre : ", select.options[select.selectedIndex].value);
    if(select.options[select.selectedIndex].value === "음식점" || select.options[select.selectedIndex].value === "카페"){
        searchBtn.innerText = select.options[select.selectedIndex].value + " 검색하기";
    } else if(select.options[select.selectedIndex].value === "뮤지컬" || select.options[select.selectedIndex].value === "연극" || select.options[select.selectedIndex].value === "기타 공연"){
        searchBtn.innerText = "공연 검색하기";
    } else {
        searchBtn.innerText = "검색하기";
    }
}

function clickSearch(){
    if (select.options[select.selectedIndex].value === "0"){
        alert("장르를 선택해주세요.");
    } else if(select.options[select.selectedIndex].value === "기타"){
        alert("기타는 검색이 불가능합니다.");
    } else {
        sessionStorage.removeItem('selectPlaceData');
        sessionStorage.removeItem('selectPerformData');
        sessionStorage.removeItem('selectAddressData');

        let formdata = loadFormDataToJson();
        formdata["place"] = "";
        formdata["subject"] = "";
        sessionStorage.setItem("form_data", JSON.stringify(loadFormDataToJson()));

        if(select.options[select.selectedIndex].value === "음식점" || select.options[select.selectedIndex].value === "카페"){
            location.href = "/review/create/search/place/0";
        } else if(select.options[select.selectedIndex].value === "기타 공연"){
            location.href = "/review/create/search/perform?genre=0";
        } else if(select.options[select.selectedIndex].value === "뮤지컬"){
            location.href = "/review/create/search/perform?genre=1";
        } else if(select.options[select.selectedIndex].value === "연극"){
            location.href = "/review/create/search/perform?genre=2";
        }
    }
}

function clickSearchAddress(){
    sessionStorage.removeItem('selectPlaceData');
    sessionStorage.removeItem('selectPerformData');
    sessionStorage.removeItem('selectAddressData');

    let place = "";
    if ($('#inputPlace').val().indexOf(" (") && $('#inputPlace').val().indexOf(")") === ($('#inputPlace').val().length - 1)) {
        place = $('#inputPlace').val().split(' (')[0];
    } else {
        place = $('#inputPlace').val();
    }

    let formdata = loadFormDataToJson();
    formdata["place"] = "";
    sessionStorage.setItem("form_data", JSON.stringify(loadFormDataToJson()));
    location.href = "/review/create/search/place/1";
}

function uploadReview() {
    console.log("inputFileList: " + inputFileList);
    let formData = new FormData();

    formData.append("reviewData", new Blob([ JSON.stringify(loadFormDataToJson()) ], {type : "application/json"}));

    for (let i = 0; i < inputFileList.length; i++) {
        formData.append("images", inputFileList[i]);  // 배열에서 이미지들을 꺼내 폼 객체에 담는다.
    }

    // document.getElementById('reviewForm').submit();
    // return false;

    $.ajax({
        url: '/review/create/upload',
        type:'POST',
        data: formData,
        enctype:"multipart/form-data",
        processData: false,
        contentType: false,
        cache: false,
        beforeSend: function (jqXHR, settings) {
            var header = $("meta[name='_csrf_header']").attr("content");
            var token = $("meta[name='_csrf']").attr("content");
            jqXHR.setRequestHeader(header, token);
        },
        success: function(data) {
            if (data == "/review/content") {
                location.reload();
            }
            sessionStorage.removeItem("form_data");
            sessionStorage.removeItem("selectPlaceData");
            sessionStorage.removeItem("selectPerformData");
            sessionStorage.removeItem("selectAddressData");
            location.href = "/review/content/" + data;
        },
        error : function(error) {
            alert("code : " + error.status + "\n" + "message : " + error.responseText + "\n" + "error : " + error);
        }
    });
}

function loadFormDataToJson() {
    let amenities = [];
    for (let i=0; i<document.getElementsByName("amenities").length; i++) {
        console.log("document.getElementsByName(amenities)[i].checked : ", document.getElementsByName("amenities")[i].checked);
        if (document.getElementsByName("amenities")[i].checked === true) {
            amenities.push(document.getElementsByName("amenities")[i].value);
        }
    }

    let grade = 0;
    if ($('input[name="grade"]:checked').val()) {
        grade = $('input[name="grade"]:checked').val();
    }

    let placeAddress = "";
    let place = "";
    if ($('#inputPlace').val().indexOf(" (") !== -1 && $('#inputPlace').val().indexOf(")") === ($('#inputPlace').val().length - 1)) {
        place = $('#inputPlace').val().split(' (')[0];
        placeAddress = $('#inputPlace').val().split(' (')[1].replace(")", "");
    } else {
        place = $('#inputPlace').val();
    }

    let form_data = {
        "genre" : $("#inputGenre option:selected").val(),
        "subject" : $('#inputTitle').val(),
        "date" : $('#inputDate').val(),
        "place" : place,
        "placeAddress" : placeAddress,
        "grade" : grade,
        "amenities" : amenities,
        "placeReview" : $('#inputPlaceReview').val(),
        "additionalReview" : $('#inputAdditionalReview').val(),
        "isAnonymous" : document.getElementById('check_anonymous').checked
    };

    console.log("form data : ", form_data);

    return form_data;
}
