function confirmUnsubscribe() {
    const confirmation = confirm("정말로 구독을 취소하시겠습니까?");
    if (confirmation) {
        const form = document.getElementById('unsubscribeForm');
        const formData = new FormData(form);

        // 계정 확인
        fetch('/v1/auth/check-account', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: formData.get('username'),
                password: formData.get('password')
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.response === true) {
                    // 계정 확인 성공 시 구독 취소 요청
                    const unsubscribeData = new FormData();
                    unsubscribeData.append('username', formData.get('username'));
                    unsubscribeData.append('value', 'false');

                    fetch('/v1/users/subscribe', {
                        method: 'PATCH',
                        body: unsubscribeData,
                    })
                        .then(response => {
                            if (response.ok) {
                                alert("구독이 성공적으로 취소되었습니다.");
                            } else {
                                alert("구독 취소에 실패했습니다. 다시 시도해주세요.");
                            }
                        })
                        .catch(error => {
                            alert("오류가 발생했습니다. 다시 시도해주세요.");
                        });
                } else {
                    alert("아이디 또는 비밀번호가 잘못되었습니다.");
                }
            })
            .catch(error => {
                alert("계정 확인 중 오류가 발생했습니다. 다시 시도해주세요.");
            });

        return false;
    } else {
        return false;
    }
}