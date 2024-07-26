document.addEventListener('DOMContentLoaded', async () => {
    const addNewsButton = document.getElementById('addNewsButton');
    const deleteNewsButton = document.getElementById('deleteNewsButton');
    const logoutButton = document.getElementById('logoutButton');
    const mainPageButton = document.getElementById('mainPageButton');
    const userManagementButton = document.getElementById('userManagementButton');
    const fetchInfoButton = document.getElementById('fetchInfoButton');
    const accessToken = localStorage.getItem('accessToken');

    // 소식 추가하기 버튼 클릭 이벤트
    addNewsButton.addEventListener('click', () => {
        // 소식 추가하기 페이지로 이동
        window.location.href = '/adm/add-news.html';
    });

    // 소식 삭제하기 버튼 클릭 이벤트
    deleteNewsButton.addEventListener('click', () => {
        // 소식 삭제하기 페이지로 이동
        window.location.href = '/adm/delete-news.html';
    });

    logoutButton.addEventListener('click', async () => {
        try {
            const response = await fetchWithAuth('/v1/auth/logout', {
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

    userManagementButton.addEventListener('click', () => {
        window.location.href = '/adm/user-management.html';
    });

    fetchInfoButton.addEventListener('click', () => {
        window.location.href = '/user-info.html';
    });
});
