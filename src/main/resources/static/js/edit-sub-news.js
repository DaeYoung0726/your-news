document.addEventListener('DOMContentLoaded', () => {
    const news1Select = document.getElementById('news1');
    const news2Select = document.getElementById('news2');
    const updateNewsButton = document.getElementById('updateNewsButton');
    const responseMessage = document.createElement('div');
    responseMessage.id = 'responseMessage';
    document.querySelector('.update-news-container').appendChild(responseMessage);

    // Populate news options
    fetch('/v1/news')
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
            const response = await fetch('/v1/users/update-sub-news', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newsNames)
            });

            const result = await response.json();

            if (response.ok) {
                responseMessage.textContent = '소식 구독 업데이트 성공.';
            } else {
                responseMessage.textContent = `Error: ${result.message}`;
            }
        } catch (error) {
            responseMessage.textContent = `Error: ${error.message}`;
        }
    });
});
