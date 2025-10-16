var pageinitalize= {
    uploadpages: function(){
        const fileInput = document.getElementById('fileInput');
        const videoPlayer = document.getElementById('videoPlayer');
        const button1 = document.getElementById('start-button');
        const button2 = document.getElementById('end-button');
        const time1 = document.getElementById('time1');
        const time2 = document.getElementById('time2');
        const submitButton = document.getElementById('submitButton');
        const resulttable = document.getElementById('resulttable');
                fileInput.addEventListener('change', (e) => {
            const file = e.target.files[0];
            const url = URL.createObjectURL(file);
            videoPlayer.src = url;
        });

		videoPlayer.addEventListener('loadedmetadata', () => {
            time1.value = 0;
            time2.value = videoPlayer.duration.toFixed(2);
        });

        button1.addEventListener('click', () => {
            time1.value = videoPlayer.currentTime.toFixed(2);
        });

        button2.addEventListener('click', () => {
            time2.value = videoPlayer.currentTime.toFixed(2);
        });

        submitButton.addEventListener('click', async () => {
             const file = fileInput.files[0];
                if (!file) {
                    alert('请上传目标视频');
                    return;
                }
            if (parseFloat(time1.value) > parseFloat(time2.value)) {
                alert('剪辑开始时间不能超过结束时间哦');
            } else {
                alert(`开始时间：${time1.value}，结束时间：${time2.value}`);
                const formData = new FormData();
                formData.append('video', file);
                formData.append('start_time', time1.value);
                formData.append('end_time', time2.value);
                try {
                    const response = await fetch('/editsetting', {
                        method: 'POST',
                        body: formData
                    });
                    if (response.ok) {
                        const result = await response.json();
                        alert(result.message);

                        location.href = "/uploadpages2";
                    } else {
                        alert('请求失败');
                    }
                } catch (error) {
                    console.error('Error:', error);
                    alert('请求出错');
                }
            }
        });
    },

    recordpages: function() {

    },

    initVideoPlayer: function() {
        const fileInput = document.getElementById('fileInput');
        const videoPlayer = document.getElementById('videoPlayer');

        fileInput.addEventListener('change', (e) => {
            const file = e.target.files[0];
            const url = URL.createObjectURL(file);
            videoPlayer.src = url;
        });
    }

}

const formData = new FormData();
                formData.append('file', blob, 'record.mp4');
                fetch('/savevideo', {
                    method: 'POST',
                    body: formData
                });
                if (response.ok) {
                    const result = await response.json();
                    alert(result.message);
                    location.href = "/recordpages2";
                } else {
                    alert('请求失败');
                }