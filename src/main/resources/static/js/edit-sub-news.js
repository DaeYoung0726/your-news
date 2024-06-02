document.addEventListener('DOMContentLoaded', () => {
    const createPostButton = document.getElementById('createPostButton');
    const fetchInfoButton = document.getElementById('fetchInfoButton');
    const logoutButton = document.getElementById('logoutButton');
    const mainPageButton = document.getElementById('mainPageButton');
    const news1Select = document.getElementById('news1');
    const news2Select = document.getElementById('news2');
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

    // Populate news options1
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
            });
        })
        .catch(error => console.error('Error fetching news options:', error));

    // Handle update news
    updateNewsButton.addEventListener('click', async () => {
        const selectedNews1 = news1Select.value;
        const selectedNews2 = news2Select.value;

        const newsNames = [];
        if (selectedNews1) newsNames.push(selectedNews1);
        if (selectedNews2) newsNames.push(selectedNews2);

        try {
            const response = await fetchWithAuth('/v1/users/update-sub-news', {
                method: 'PUT',
                headers: {
                    'Authorization': accessToken,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newsNames)
            });


            if (response.ok) {
                alert('소식 구독 업데이트 성공.');
                window.location.href = '/user-info.html';
            } else {
                const result = await response.json();
                if (typeof result === 'object' && result !== null) {
                    const errorMessages = Object.entries(result).map(([field, message]) => `${message}`).join('\n');
                    alert(`Error:\n${errorMessages}`);
                } else {
                    alert(`Error: 알 수 없는 오류가 발생했습니다.`);
                }
            }
        } catch (error) {
            responseMessage.textContent = `Error: ${error.message}`;
        }
    });
});
