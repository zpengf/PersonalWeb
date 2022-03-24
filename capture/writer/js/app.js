window.app = {



    /* 
    portalIndexUrl: "http://localhost:8080/capture/portal/index.html",           // 门户首页地址
    writerIndexUrl: "http://localhost:8080/capture/writer/contentMng.html",      // 作家中心首页
    writerInfoUrl: "http://localhost:8080/capture/writer/accountInfo.html",     // 用户信息完善页面
    userServerUrl: "http://192.168.1.5:8003",   // 用户服务后端接口地址
    */

    portalIndexUrl: "http://capturez.xyz:9090/capture/portal/index.html",           // 门户首页地址
    writerLoginUrl: "http://capturez.xyz:9090/capture/writer/passport.html",      // 登录页面
    writerIndexUrl: "http://capturez.xyz:9090/capture/writer/contentMng.html",      // 作家中心首页
    writerInfoUrl: "http://capturez.xyz:9090/capture/writer/accountInfo.html",     // 用户信息完善页面
    adminCenterUrl: "http://capturez.xyz:9090/capture/admin/contentReview.html",     // 运营管理平台主页

    
    userServerUrl: "http://capturez.xyz:8003",        // 用户服务后端接口地址
    fsServerUrl: "http://capturez.xyz:8004",         // 文件服务后端接口地址
    adminServerUrl: "http://capturez.xyz:8005",      // 运营管理服务后端接口地址
    articleServerUrl: "http://capturez.xyz:8001",      // 文章服务后端接口地址

    /**
     * 如果本地使用localhost测试可以不使用，如果是ip或者域名测试，cookieDomain改为对应的ip或者域名
     */
    cookieDomain: "capturez.xyz",  


    // writer媒体中心，每个页面都需要在进入的时候判断有没有登录，如果没有，退出到登录页面
    judgeWriterLogin() {
        var me = this;
        var utoken = me.getCookie("utoken");
        var uid = me.getCookie("uid");

        if ( me.isEmpty(utoken) || me.isEmpty(uid)) {
            alert("请登录后再使用媒体中心的相关页面！");
            window.location = me.writerLoginUrl;
        }
    },

     // 判断用户是否登录
    judgeUserLoginStatus: function(pageVue) {
        var me = this;
        var utoken = me.getCookie("utoken");
        var uid = me.getCookie("uid");
        
        // console.log("utoken=" + utoken);
        // console.log("uid=" + uid);

        // utoken和uid都存在与cookie中，说明用户是登录状态
        if ( me.isNotEmpty(utoken) && me.isNotEmpty(uid)) {
            
            // 从session storage中获得用户信息，如果没有，则发起请求从后端获取
            var userInfo = me.fetchUserInfo();
            if (me.isNotEmpty(userInfo)) {
                // 用户存在，首页显示用户信息
                pageVue.userInfo = userInfo;
            } else {
                // 用户不存在，发起请求调用后端
                var userServerUrl = me.userServerUrl;
                axios.post(
                        userServerUrl + '/user/getUserInfo?userId=' + uid,
                        {}, 
                        {
                            headers: {
                                'headerUserId': uid,
                                'headerUserToken': utoken
                            }
                        })
                    .then(res => {
                        // debugger
                        if (res.data.status == 200) {
                            var userInfo = res.data.data;
                            // console.log("app:" + userInfo);
                            // 获得到用户信息后，还需要判断用户的状态，以防管理员对其封号后，状态不会检测
                            var activeStatus = userInfo.activeStatus;
                            // 如果是被冻结状态，则退出登录
                            if (activeStatus == 2) {
                                alert("您的账号因违规操作被封...");
                                // me.logout(pageVue);
                                return false;
                            }
                            // 保存到session storage
                            // me.saveUserInfo(userInfo);
                            // 首页显示用户信息
                            pageVue.userInfo = userInfo;
                            return userInfo;
                        } else {
                            console.log(res.data.msg);
                            return false;
                        }
                    });
            }
        } else {
            // token和uid都没有，表示用户没有登录过
            return false;
        }
    },

    logout(pageVue) {
        var me = this;
        var uid = me.getCookie("uid");
        var userServerUrl = me.userServerUrl;
        axios.post(
                userServerUrl + '/passport/logout?userId=' + uid)
            .then(res => {
                if (res.data.status == 200) {
                    // 删除sessionStorage中用户信息
                    me.deleteUserInfo();
                    // 删除用户cookie
                    me.deleteCookie("utoken");
                    me.deleteCookie("uid");
                    // 设置用户信息为空
                    pageVue.userInfo = null;
                    console.log("用户已退出");
                } else {
                    console.log(res.data.msg);
                }
            });
    },

    // 保存用户信息在 sessionStorage (保存数据的时间有效周期: 从打开浏览器到关闭浏览器)
    // localStorage 是永久存在，对于用户信息不适合存放
    // cookie 用于存放用户信息也不太好，而且cookie的大小限制为4k
    saveUserInfo: function(userInfo) {
        var userInfoStr = JSON.stringify(userInfo);
        sessionStorage.setItem("globalUserInfo", userInfoStr);
    },
    // 从sessionStorage中读取用户信息
    fetchUserInfo: function() {
        var userInfoStr = sessionStorage.getItem("globalUserInfo");
        return JSON.parse(userInfoStr);
    },
    // 从sessionStorage中删除用户信息
    deleteUserInfo: function() {
        sessionStorage.removeItem("globalUserInfo");
    },

    isEmpty: function (str) {
        if (str == null || str =="" || str == undefined) {
            return true;
        } else {
            return false;
        }
    },

    isNotEmpty: function (str) {
        if (str != null && str !="" && str != undefined) {
            return true;
        } else {
            return false;
        }
    },

    getCookie: function (cname) {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            // console.log(c)
            while (c.charAt(0) == ' ') c = c.substring(1);
                if (c.indexOf(name) != -1){
                    return c.substring(name.length, c.length);
                }
            }
        return "";
    },

    setCookie: function(name, value) {
        var Days = 365;
        var exp = new Date(); 
        exp.setTime(exp.getTime() + Days*24*60*60*1000);
        var cookieContent = name + "="+ encodeURIComponent (value) + ";path=/;";
        if (this.cookieDomain != null && this.cookieDomain != undefined && this.cookieDomain != '') {
            cookieContent += "domain=" + this.cookieDomain;
        }
        document.cookie = cookieContent + cookieContent;
        // document.cookie = name + "="+ encodeURIComponent (value) + ";path=/;domain=" + cookieDomain;//expires=" + exp.toGMTString();
    },

    deleteCookie: function(name) {
        var cookieContent = name + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        if (this.cookieDomain != null && this.cookieDomain != undefined && this.cookieDomain != '') {
            cookieContent += "domain=" + this.cookieDomain;
        }
        document.cookie = cookieContent;
    },

    getUrlParam(paramName) {
        var reg = new RegExp("(^|&)" + paramName + "=([^&]*)(&|$)");    //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);            //匹配目标参数
        if (r != null) return decodeURI(r[2]); return null;             //返回参数值
    },

    checkMobile(mobile) {
        var myreg = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
        if (!myreg.test(mobile)) {
            return false;
        }
        return true;
    },

    checkEmail(email) {
        var myreg = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
        if (!myreg.test(email)) {
            return false;
        }
        return true;
    },

    checkMobile(mobile) {
        var myreg =/^1(3\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\d|9[0-35-9])\d{8}$/;
        if (!myreg.test(mobile)) {
            return false;
        }
        return true;
    },
}
