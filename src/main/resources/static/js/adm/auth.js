document.addEventListener('DOMContentLoaded', async () => {
    const accessToken = localStorage.getItem('accessToken');


    if (!accessToken) {
        alert('로그인 정보가 없습니다. 로그인 페이지로 이동합니다.');
        window.location.href = '/'; // 로그인 페이지로 이동
    }

    async function checkAuthorization() {


        try {
            // 사용자 권한 확인
            const roleResponse = await fetch('/v1/auth/user-role', {
                method: 'GET',
                headers: {
                    'Authorization': accessToken,
                    'Content-Type': 'application/json'
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
            window.location.href = '/';
        }
    }

    // 페이지 로드 시 권한 확인
    await checkAuthorization();
});


async function fetchWithAuth(url, options = {}) {
    let accessToken = localStorage.getItem('accessToken');

    // 기본 헤더 설정
    if (!options.headers) {
        options.headers = {};
    }
    options.headers['Authorization'] = accessToken
    options.headers['Content-Type'] = 'application/json';

    let response = await fetch(url, options);

    // 401 오류가 발생하면 토큰 재발급 시도
    if (response.status === 401) {
        const refreshResponse = await fetch('/v1/auth/reissue', {
            method: 'POST',
            credentials: 'include'
        });

        if (refreshResponse.ok) {
            const data = await refreshResponse.json();
            newAccessToken = data.newAccessToken;
            localStorage.setItem('accessToken', newAccessToken);

            // 새로 발급받은 토큰으로 원래 요청 재시도
            options.headers['Authorization'] = newAccessToken;
            response = await fetch(url, options);
        } else {
            window.location.href = '/'; // 리프레시 토큰 실패 시 로그인 페이지로 이동
        }
    }

    return response;
}


