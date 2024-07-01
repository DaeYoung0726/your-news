document.addEventListener('DOMContentLoaded', () => {
    const createPostButton = document.getElementById('createPostButton');
    const fetchInfoButton = document.getElementById('fetchInfoButton');
    const logoutButton = document.getElementById('logoutButton');
    const mainPageButton = document.getElementById('mainPageButton');
    const news1Select = document.getElementById('news1');
    const news2Select = document.getElementById('news2');
    const news3Select = document.getElementById('news3');
    const updateNewsButton = document.getElementById('updateNewsButton');
    const responseMessage = document.createElement('div');
    const accessToken = localStorage.getItem('accessToken');

    responseMessage.id = 'responseMessage';
    document.querySelector('.update-news-container').appendChild(responseMessage);

    createPostButton.addEventListener('click', () => {
        window.location.href = '/create-post.html';
    });

    fetchInfoButton.addEventListener('click', () => {
        window.location.href = '/user-info.html';
    });

    logoutButton.addEventListener('click', async () => {
        try {
            const response = await fetchWithAuth('/logout', {
                method: 'POST',
                headers: {
                    'Authorization': accessToken,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                window.location.href = '/';
            } else {
                alert('로그아웃 실패. 다시 시도해주세요.');
            }
        } catch (error) {
            alert(`Error: ${error.message}`);
        }
    });

    mainPageButton.addEventListener('click', () => {
        window.location.href = '/main.html';
    });

    // 소식 불러오기
    fetchWithAuth('/v1/news')
        .then(response => response.json())
        .then(data => {
            data.forEach(news => {
                let option1 = document.createElement('option');
                option1.text = news.newsName;
                news1Select.add(option1);

                let option2 = document.createElement('option');
                option2.text = news.newsName;
                news2Select.add(option2);

                let option3 = document.createElement('option');
                option3.text = news.newsName;
                news3Select.add(option3);
            });
        })
        .catch(error => console.error('Error fetching news options:', error));

    // 소식 구독 업데이트
    updateNewsButton.addEventListener('click', async () => {
        const selectedNews1 = news1Select.value;
        const selectedNews2 = news2Select.value;
        const selectedNews3 = news3Select.value;

        const newsNames = [];
        if (selectedNews1) newsNames.push(selectedNews1);
        if (selectedNews2) newsNames.push(selectedNews2);
        if (selectedNews3) newsNames.push(selectedNews3);

        try {
            const response = await fetchWithAuth('/v1/users/update-sub-news', {
                method: 'PUT',
                headers: {
                    'Authorization': accessToken,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newsNames)
            });


            const result = await response.json();
            if (response.ok) {
                alert(result.response);
                window.location.href = '/user-info.html';
            } else {
                alert(result.message);
            }
        } catch (error) {
            responseMessage.textContent = `Error: ${error.message}`;
        }
    });
});
