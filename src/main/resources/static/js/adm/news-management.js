document.addEventListener('DOMContentLoaded', async () => {
    const addNewsButton = document.getElementById('addNewsButton');
    const deleteNewsButton = document.getElementById('deleteNewsButton');
    const logoutButton = document.getElementById('logoutButton');
    const mainPageButton = document.getElementById('mainPageButton');
    const userManagementButton = document.getElementById('userManagementButton');
    const fetchInfoButton = document.getElementById('fetchInfoButton');

    async function checkAuthorization() {
        const accessToken = localStorage.getItem('accessToken');

        try {
            // 사용자 권한 확인
            const roleResponse = await fetch('/v1/auth/user-role', {
                method: 'GET',
                headers: {
                    'Authorization': accessToken
                }
            });

            if (!roleResponse.ok) {
                throw new Error('권한 확인 실패');
            }

            const roleData = await roleResponse.json();
            if (roleData.role !== 'ADMIN') {
                alert('권한이 없습니다.');
                window.location.href = '/main.html';
            }
        } catch (error) {
            alert('권한이 없습니다. 로그인 페이지로 이동합니다.');
            window.location.href = '/index.html';
        }
    }

    // 페이지 로드 시 권한 확인
    await checkAuthorization();

    // 소식 추가하기 버튼 클릭 이벤트
    addNewsButton.addEventListener('click', () => {
        // 소식 추가하기 페이지로 이동
        window.location.href = '/add-news.html';
    });

    // 소식 삭제하기 버튼 클릭 이벤트
    deleteNewsButton.addEventListener('click', () => {
        // 소식 삭제하기 페이지로 이동
        window.location.href = '/delete-news.html';
    });

    // 기타 버튼 이벤트 리스너
    logoutButton.addEventListener('click', () => {
        window.location.href = '/logout';
    });

    mainPageButton.addEventListener('click', () => {
        window.location.href = '/main.html';
    });

    userManagementButton.addEventListener('click', () => {
        window.location.href = '/user-management.html';
    });

    fetchInfoButton.addEventListener('click', () => {
        window.location.href = '/user-info.html';
    });
});
