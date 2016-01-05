package com.yzx.lifeassistants.common;

/**
 * @Description: 存放常量
 * @author: yzx
 * @time: 2015-9-16 下午3:01:49
 */
public class CommonConstant {
	/**
	 * @Description: 动态特效浅颜色
	 */
	public static final String RIPPLE_COLOR_LITHT = "#b8b2b2";
	/**
	 * @Description: 动态特效深颜色
	 */
	public static final String RIPPLE_COLOR_DARK = "#586a42";
	/**
	 * @Description: 动态特效透明度
	 */
	public static final Float RIPPLE_ALPHA = 0.2f;

	// ---------------------------------ID密钥------------------------------------//
	/**
	 * @Description: BMOB_SDK‘s APP_ID
	 */
	public static final String BMOB_APP_ID = "09d8be7fe2dac8facfff0cee534e9682";
	/**
	 * @Description: 微信分享ID信息
	 */
	public static final String WEIXIN_APP_ID = "wx1fafb5587ceb9e7e";
	/**
	 * @Description: 微信分享密钥信息
	 */
	public static final String WEIXIN_APP_SECRET = "d4624c36b6795d1d99dcf0547af5443d";
	/**
	 * @Description: QQ分享ID信息
	 */
	public static final String QQ_APP_ID = "1104957398";
	/**
	 * @Description: QQ分享密钥信息
	 */
	public static final String QQ_APP_SECRET = "RNeWvrHp1grXJiCJ";
	/**
	 * @Description: 微博关注人UID
	 */
	public static final String SINA_UID = "5533709282";
	/**
	 * @Description: 微博分享ID信息
	 */
	public static final String SINA_APP_ID = "2453221922";
	/**
	 * @Description: 微博分享密钥信息
	 */
	public static final String SINA_APP_SECRET = "6e8d8bb4291d9be56f8ea428f9ba7e44";
	/**
	 * @Description: 微博分享重定向URL
	 */
	public static final String SINA_REDIRECT_URL = "http://www.sharesdk.cn";
	// ---------------------------------分享信息------------------------------------//
	/**
	 * @Description: 分享Title
	 */
	public static final String SHARE_TITLE = "赶紧来下载吧";
	/**
	 * @Description: 分享URL
	 */
	public static final String SHARE_TARGET_URL = "http://zhaowoapp.bmob.cn/";
	/**
	 * @Description: 分享内容
	 */
	public static final String SHARE_CONTENT = "找我整合了二手交易，失物招领，记账能手，简洁好用，推荐你也来用用！";
	/**
	 * @Description: 摇一摇分享内容
	 */
	public static final String SHAKE_SHARE_CONTENT = "美好瞬间，摇摇分享——来自找我摇一摇分享";
	/**
	 * @Description: 安智市场下载地址
	 */
	public static final String DOWNLOAD_URL = "http://www.anzhi.com/soft_2487961.html";
	// ---------------------------------登陆注册------------------------------------//
	/**
	 * @Description: 登陆页面传到注册页面的requestCode
	 */
	public static final int REQUESTCODE_USER_REG = 0;

	/**
	 * @Description: 登陆页面传到注册页面的requestCode
	 */
	public static final int RESULTCODE_USER_REG_OK = 10;
	// ---------------------------------失物招领------------------------------------//

	/**
	 * @Description: 从LostAndFindFragment传到AddLostActivity请求添加失物信息
	 */
	public static final String FROM_LOSTANDFRAGMENT = "ADD_LOST";

	/**
	 * @Description: 从LostAndFindFragment传到AddLostActivity请求添加招领信息
	 */
	public static final String FROM_ANDFINDFRAGMENT = "ADD_FIND";
	/**
	 * @Description: 由失物/招领页面传到添加/修改页面的requestCode
	 */
	public static final int REQUESTCODE_ADD_LOST_FIND = 1;
	/**
	 * @Description: 由添加/修改界面传到失物找寻页面的resultCode
	 */
	public static final int RESULTCODE_NEW_LOST_OK = 2;
	/**
	 * @Description: 由添加/修改界面传到拾取招领页面的resultCode
	 */
	public static final int RESULTCODE_NEW_FIND_OK = 3;
	/**
	 * @Description: 从LostFragment传到LostAndFindActivity请求查看/修改失物信息
	 */
	public static final String FROM_LOSTFRAGMENT = "CHECK_LOST";

	/**
	 * @Description: 从FindFragment传到LostAndFindActivity请求查看/修改招领信息
	 */
	public static final String FROM_FINDFRAGMENT = "CHECK_FIND";
	/**
	 * @Description: 由失物页面传到查看/修改页面的requestCode
	 */
	public static final int REQUESTCODE_SEE_LOST = 132;
	/**
	 * @Description: 由招领页面传到查看/修改页面的requestCode
	 */
	public static final int REQUESTCODE_SEE_FIND = 131;

	/**
	 * @Description: 由修改/添加界面传到失物找寻页面的resultCode
	 */
	public static final int RESULTCODE_UPDATE_LOST_OK = 5;
	/**
	 * @Description: 由修改/添加界面传到拾取招领页面的resultCode
	 */
	public static final int RESULTCODE_UPDATE_FIND_OK = 6;
	/**
	 * @Description: 由修改/添加界面传到失物找寻页面的resultCode
	 */
	public static final int RESULTCODE_DELETE_LOST_OK = 8;
	/**
	 * @Description: 由修改/添加界面传到拾取招领页面的resultCode
	 */
	public static final int RESULTCODE_DELETE_FIND_OK = 9;
	// ---------------------------------借入借出------------------------------------//
	/**
	 * @Description: 从BorrowAndLendFragment传到BorrowActivity请求添加借入信息
	 */
	public static final String FROM_BORROWANDFRAGMENT = "ADD_BORROW";

	/**
	 * @Description: 从BorrowAndLendFragment传到LendActivity请求添加借出信息
	 */
	public static final String FROM_ANDLENDFRAGMENT = "ADD_LEND";

	/**
	 * @Description: 由借入/借出页面传到添加/修改页面的requestCode
	 */
	public static final int REQUESTCODE_ADD_BORROW_LEND = 121;
	/**
	 * @Description: 由添加/修改页面传到借入页面的resultCode
	 */
	public static final int RESULTCODE_NEW_BORROW_OK = 122;
	/**
	 * @Description: 由添加/修改页面传到借出页面的resultCode
	 */
	public static final int RESULTCODE_NEW_LEND_OK = 123;
	/**
	 * @Description: 从BorrowFragment传到BorrowAndLendActivity请求查看/修改借入信息
	 */
	public static final String FROM_BORROWFRAGMENT = "CHECK_BORROW";
	/**
	 * @Description: 从LendFragment传到BorrowAndLendActivity请求查看/修改借出信息
	 */
	public static final String FROM_LENDFRAGMENT = "CHECK_LEND";
	/**
	 * @Description: 由借入页面传到查看/修改页面的requestCode
	 */
	public static final int REQUESTCODE_SEE_BORROW = 124;
	/**
	 * @Description: 由借出页面传到查看/修改页面的requestCode
	 */
	public static final int REQUESTCODE_SEE_LEND = 125;

	/**
	 * @Description: 由修改/添加界面传到借入明细页面的resultCode
	 */
	public static final int RESULTCODE_UPDATE_BORROW_OK = 126;
	/**
	 * @Description: 由修改/添加界面传到借出明细页面的resultCode
	 */
	public static final int RESULTCODE_UPDATE_LEND_OK = 127;
	/**
	 * @Description: 由修改/添加界面传到借入明细页面的resultCode
	 */
	public static final int RESULTCODE_DELETE_BORROW_OK = 128;
	/**
	 * @Description: 由修改/添加界面传到借出明细页面的resultCode
	 */
	public static final int RESULTCODE_DELETE_LEND_OK = 129;
	// ---------------------------------意见反馈------------------------------------//
	/**
	 * @Description: 设置界面传到更改意见反馈界面的请求码
	 */
	public static final int REQUESTCODE_FEEDBACK = 110;
	/**
	 * @Description: 意见反馈界面传到设置界面的结果码
	 */
	public static final int RESULTCODE_FEEDBACK_OK = 111;
	/**
	 * @Description: 意见反馈界面传到设置界面的结果码
	 */
	public static final int RESULTCODE_FEEDBACK_CANCEL = 112;
	// ---------------------------------更改昵称------------------------------------//
	/**
	 * @Description: 设置界面传到更改昵称界面的请求码
	 */
	public static final int REQUESTCODE_EDIT_NICK = 100;
	/**
	 * @Description: 更改昵称界面传到设置界面的结果码
	 */
	public static final int RESULTCODE_EDIT_NICK_OK = 101;
	/**
	 * @Description: 更改昵称界面传到设置界面的结果码
	 */
	public static final int RESULTCODE_EDIT_NICK_CANCEL = 102;
	// ---------------------------------保存属性------------------------------------//
	/**
	 * @Description: SharedPreferences存储的文件名
	 */
	public static final String SP_FAIL_NAME = "ZHAOWO_SP";
	/**
	 * @Description: SharedPreferences存储是否记住密码的key
	 */
	public static final String REMBER_PWD_KEY = "REMBER_PWD";
	/**
	 * @Description: SharedPreferences存储是否自动登录的key
	 */
	public static final String AUTO_LOGIN_KEY = "AUTO_LOGIN";
	/**
	 * @Description: SharedPreferences存储username的key
	 */
	public static final String USERNAME_KEY = "USERNAME";
	/**
	 * @Description: SharedPreferences存储nick的key
	 */
	public static final String NICK_KEY = "NICK";
	/**
	 * @Description: SharedPreferences存储password的key
	 */
	public static final String PASSWORD_KEY = "PASSWORD";
	// ---------------------------------修改密码------------------------------------//
	/**
	 * @Description: 传到登陆界面的name
	 */
	public static final String TO_LOGIN_NAME = "MODIFY2LOGIN_NAME";
	/**
	 * @Description: 从修改密码页面传到登陆界面的key
	 */
	public static final String FROM_MODIFY_TO_LOGIN_KEY = "MODIFY2LOGIN_KEY";
	// ---------------------------------注销登陆------------------------------------//
	/**
	 * @Description: 从设置页面的注销用户传到登陆界面的name
	 */
	public static final String FROM_LOGOUT_TO_LOGIN_KEY = "LOGOUT2LOGIN_KEY";
	// ---------------------------------更改头像------------------------------------//
	/**
	 * @Description: 头像存储在sdcard的名称
	 */
	public static final String PHOTO_FILE_NAME = "temp_photo.jpg";
	/**
	 * @Description: 设置页面访问相机的请求码
	 */
	public static final int REQUESTCODE_PHOTO_CAMERA = 103;
	/**
	 * @Description: 设置页面访问相册的请求码
	 */
	public static final int REQUESTCODE_PHOTO_ALBUM = 104;
	/**
	 * @Description: 设置页面切图的请求码
	 */
	public static final int REQUESTCODE_PHOTO_CUT = 105;
	// ---------------------------------二手市场------------------------------------//
	/**
	 * @Description: 从二手市场界面点击添加按钮进入编辑界面进行新的闲置物品发布
	 */
	public static final String FROM_SECOND_HAND_FRAGMENT = "ADD_SECOND_HAND";
	/**
	 * @Description: 从闲置物品详情界面点击修改按钮进入编辑界面对之前发布过的闲置物品进行修改
	 */
	public static final String FROM_SECOND_HAND_MY_FRAGMENT = "MODIFY_SECOND_HAND";
	/**
	 * @Description: 从二手市场界面点击添加按钮进入编辑界面进行新的闲置物品发布的新增请求码
	 */
	public static final int REQUESTCODE_ADD_SECOND_HAND = 1;
	/**
	 * @Description: 从编辑界面点击发布按钮进入二手市场界面的新增结果码
	 */
	public static final int RESULTCODE_ADD_SECOND_HAND_OK = 2;
	/**
	 * @Description: 从二手市场我的界面点击列表项进入编辑界面进行新的闲置物品发布的修改请求码
	 */
	public static final int REQUESTCODE_MODIFY_SECOND_HAND = 3;
	/**
	 * @Description: 从编辑界面点击发布按钮进入二手市场我的界面的修改结果码
	 */
	public static final int RESULTCODE_MODIFY_SECOND_HAND_OK = 4;
	/**
	 * @Description: 进入闲置物品详情界面的KEY
	 */
	public static final String TO_SECOND_HAND_DETAIL_ACTIVITY_KEY = "TO_SECOND_HAND_DETAIL_ACTIVITY_KEY";
	/**
	 * @Description: 从闲置物品详情界面传递给闲置物品物品/联系信息界面的KEY
	 */
	public static final String FROM_SECOND_HAND_DETAIL_ACTIVITY_KEY = "FROM_SECOND_HAND_DETAIL_ACTIVITY_KEY";

	// ---------------------------------记账能手------------------------------------//
	/**
	 * @Description: 从记账能手界面传到设置生活费指标界面的请求码
	 */
	public static final int REQUESTCODE_SET_ALIMONY = 1;
	/**
	 * @Description: 从设置生活费指标传回记账能手界面的结果码
	 */
	public static final int RESULTCODE_SET_ALIMONY_SUCCESS = 2;
	/**
	 * @Description: 从记账能手界面传到添加支出记录界面的请求码
	 */
	public static final int REQUESTCODE_ADD_EXPEND = 3;
	/**
	 * @Description: 从添加支出记录界面传回记账能手界面的结果码
	 */
	public static final int RESULTCODE_ADD_EXPEND_SUCCESS = 4;
	/**
	 * @Description: 从记账能手界面传递给添加支出界面余额等级
	 */
	public static final String TO_ADD_EXPEND_ACTIVITY_BALANCE_LEVEL = "TO_ADD_EXPEND_ACTIVITY_BALANCE_LEVEL";
	/**
	 * @Description: 从记账能手界面传递给添加支出界面表示新增
	 */
	public static final String TO_ADD_EXPEND_ACTIVITY_IS_NEW = "TO_ADD_EXPEND_ACTIVITY_IS_NEW";
	/**
	 * @Description: 从记账能手界面传递给添加支出界面表示修改
	 */
	public static final String TO_ADD_EXPEND_ACTIVITY_IS_MODIFY = "TO_ADD_EXPEND_ACTIVITY_IS_MODIFY";
	/**
	 * @Description: 从账单柱形图分析界面传到添加支出记录界面的请求码
	 */
	public static final int REQUESTCODE_MODIFY_EXPEND = 5;
	/**
	 * @Description: 从添加支出记录界面传回账单柱形图分析界面的结果码
	 */
	public static final int RESULTCODE_MODIFY_EXPEND_SUCCESS = 6;
	/**
	 * @Description: 从记账能手界面传递给支出图表分析界面的KEY
	 */
	public static final String TO_EXPEND_ANALYSIS_ACTIVITY_KEY = "TO_EXPEND_ANALYSIS_ACTIVITY_KEY";
	/**
	 * @Description: 从支出饼图分析界面传递给年支出界面的KEY
	 */
	public static final String TO_EXPEND_YEAR_ACTIVITY_KEY = "TO_EXPEND_YEAR_ACTIVITY_KEY";
	/**
	 * @Description: 传递到月支出界面的KEY
	 */
	public static final String TO_EXPEND_MONTH_ACTIVITY_KEY = "TO_EXPEND_MONTH_ACTIVITY_KEY";
	/**
	 * @Description: 传递到月支出分析界面的KEY
	 */
	public static final String TO_EXPEND_MONTH_ANALYSIS_ACTIVITY_KEY = "TO_EXPEND_MONTH_ANALYSIS_ACTIVITY_KEY";
	/**
	 * @Description: 从月支出分析界面传递到Fragment的KEY
	 */
	public static final String FROM_EXPEND_MONTH_ANALYSIS_ACTIVITY_KEY = "FROM_EXPEND_MONTH_ANALYSIS_ACTIVITY_KEY";
	/**
	 * @Description: 传递到日支出界面的KEY
	 */
	public static final String TO_EXPEND_DATE_ACTIVITY_KEY = "TO_EXPEND_DATE_ACTIVITY_KEY";
}
