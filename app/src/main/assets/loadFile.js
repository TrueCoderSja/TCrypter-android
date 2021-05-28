function loadFile(filePath){
    let imageTypes=["png", "jpg", "jpeg", "gif", "bmp", "jfif"];
    let videoTypes=["mp4", "avi", "flv", "mov", "mkv", "webm", "wmv"];
    let audioTypes=["mp3", "wav", "m4a", "flac", "wma", "aac"];

    document.getElementById("iconDivison").style.display="block";
    let icon=document.getElementById("fileIcon");
    let ext=filePath.split('.').pop().toLowerCase();
    if(imageTypes.includes(ext))
        icon.src="image.svg";
    else if(videoTypes.includes(ext))
        icon.src="video.svg";
    else if(audioTypes.includes(ext))
        icon.src="audio.svg"
    else if(ext=="apk")
        icon.src="apk.png";
    else
        icon.src="file.svg";

    let fileName=filePath.split("/").pop().split("\\").pop();
    document.getElementById("fileName").innerText=fileName;
}