document.addEventListener('DOMContentLoaded', async () => {
    const logoutButton = document.getElementById('logoutButton');
    const mainPageButton = document.getElementById('mainPageButton');
    const newsManagementButton = document.getElementById('newsManagementButton');
    const userManagementButton = document.getElementById('userManagementButton');
    const fetchInfoButton = document.getElementById('fetchInfoButton');
    const newsIdElement = document.getElementById('newsId');
    const newsNameElement = document.getElementById('newsName');
    const newsURLElement = document.getElementById('newsURL');
    const subMemberSizeElement = document.getElementById('subMemberSize');
    const deleteNewsButton = document.getElementById('deleteNewsButton');
    const accessToken = localStorage.getItem('accessToken');

    const urlParams = new URLSearchParams(window.location.search);
    const newsId = urlParams.get('newsId');


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

    newsManagementButton.addEventListener('click', () => {
        window.location.href = '/adm/news-management.html'
    })

    userManagementButton.addEventListener('click', () => {
        window.location.href = '/user-management.html';
    });

    fetchInfoButton.addEventListener('click', () => {
        window.location.href = '/user-info.html';
    });

    async function fetchNewsDetails() {
        try {
            const response = await fetchWithAuth(`/v1/admin/news/${newsId}`, {
                method: 'GET',
                headers: {
                    'Authorization': accessToken,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const newsData = await response.json();
                displayNewsDetails(newsData);
            } else {
                alert('소식 정보를 불러오는 데 실패했습니다.');
            }
        } catch (error) {
            alert('소식 정보를 불러오는 중 오류가 발생했습니다: ' + error.message);
        }
    }

    function displayNewsDetails(newsData) {
        newsIdElement.textContent = newsData.id;
        newsNameElement.textContent = newsData.newsName;
        newsURLElement.textContent = newsData.newsURL;
        subMemberSizeElement.textContent = newsData.subMemberSize;
    }

    async function deleteNews() {

        try {
            const response = await fetchWithAuth(`/v1/admin/news/${newsId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': accessToken,
                    'Content-Type': 'application/json'
                }
            });

                const result = await response.json();
            if (response.ok) {
                alert(result.response);
                window.location.href = '/adm/delete-news.html';
            } else {
                alert(result.message);
            }
        } catch (error) {
            alert('소식 삭제 중 오류가 발생했습니다: ' + error.message);
        }
    }

    deleteNewsButton.addEventListener('click', () => {
        if (confirm('정말로 이 소식을 삭제하시겠습니까?')) {
            deleteNews();
        }
    });

    fetchNewsDetails();
});
