<#include "/common/commoncss.ftl">
<link rel="stylesheet" type="text/css" href="css/common/checkbox.css"/>
<style type="text/css">
    a {
        color: black;
    }

    a:hover {
        text-decoration: none;
    }

    .bgc-w {
        background-color: #fff;
    }
</style>
<div class="row" style="padding-top: 10px;">
    <div class="col-md-2">
        <h1 style="font-size: 24px; margin: 0;" class="">状态管理</h1>
    </div>
    <div class="col-md-10 text-right">
        <a href="##"><span class="glyphicon glyphicon-home"></span> 首页</a>
        <a disabled="disabled">状态管理</a>
    </div>
</div>
<div class="row" style="padding-top: 15px;">
    <div class="col-md-12">
        <!--id="container"-->
        <div class="bgc-w box">
            <!--盒子头-->
            <div class="box-header">
                <h3 class="box-title">
                    <a href="javascript:history.back();" class="label label-default"
                       style="padding: 5px;">
                        <i class="glyphicon glyphicon-chevron-left"></i> <span>返回</span>
                    </a>
                </h3>
            </div>
            <!--盒子身体-->
            <div class="box-body no-padding">
                <div class="box-body">
                    <div class="alert alert-danger alert-dismissable" role="alert" style="display: none;">
                        错误信息:
                        <button class="close" type="button">&times;</button>
                        <span class="error-mess"></span>
                    </div>
                    <div class="row">
                        <div class="col-md-6 form-group">
                            <label class="control-label"><span>模块名</span></label>
                            <br>
                            <label class="control-label"><span>${(status.statusModel)!''}</span></label>
                        </div>
                        <div class="col-md-6 form-group">
                            <label class="control-label"><span>状态</span></label>
                            <br>
                            <label class="control-label"><span>${(status.statusName)!''}</span></label>
                        </div>
                        <div class="col-md-6 form-group">
                            <label class="control-label"><span>排序值</span></label>
                            <br>
                            <label class="control-label"><span>${(status.statusSortValue)!''}</span></label>

                        </div>
                        <div class="col-md-6 form-group">
                            <label class="control-label"><span>颜色</span></label>
                            <br>
                            <label class="control-label"><span>${(status.statusColor)!''}</span></label>
                        </div>

                    </div>

                </div>
            </div>

        </div>
    </div>
</div>
<#include "/common/modalTip.ftl">