package com.lxg.exams.controller.usercontroller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxg.exams.bean.Question;
import com.lxg.exams.mapper.QuestionMapper;
import com.lxg.exams.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;


@RestController
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;


    @PostMapping
    public Boolean addQue(@RequestBody Question question, HttpSession session) {
        System.out.println(question + "===============================================");
        question.setUid((Integer) session.getAttribute("uid"));
        return questionService.save(question);
    }

    //分页查询错题
    @GetMapping("/{page}/{pageSize}")
    public Page page(@PathVariable int page, @PathVariable int pageSize, Question question, HttpSession session) {
        Integer uid = (Integer) session.getAttribute("uid");
        Page quePage = new Page(page, pageSize);
        LambdaQueryWrapper<Question> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Question::getIsdeleted, 0);
        lqw.eq(Question::getUid, uid);
        lqw.eq(question.getTypes() != null, Question::getTypes, question.getTypes());
        lqw.like(question.getTitle() != null, Question::getTitle, question.getTitle());
        questionService.page(quePage, lqw);
        return quePage;
    }

    //修改错题
    @PutMapping
    public Boolean updateQue(@RequestBody Question question) {
        //设置更新时间
        //获取当前格式化时间
        SimpleDateFormat sdf  =  new  SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        String  updateTime  =  sdf.format(new Date());
        question.setUpdateTime(updateTime);
        boolean b = questionService.updateById(question);
        return b;
    }

    @DeleteMapping("/{id}")
    //删除错题
    public Boolean deleteQue(@PathVariable Integer id) {
        Question question = questionService.getById(id);
        question.setIsdeleted(1);
        boolean b = questionService.updateById(question);
        return b;
    }


    @PostMapping("/public/{id}")
    //设置题目为公开或者私有
    public Boolean setQuestionPublicById(@PathVariable Integer id) {
        Question question = questionService.getById(id);
        Integer ispublic = question.getIspublic();
        if (ispublic == 1) {
            question.setIspublic(0);
        } else {
            question.setIspublic(1);
        }
        boolean b = questionService.updateById(question);
        return b;
    }


    //根据id查询题目是否已经公开
    @GetMapping("/status/{id}")
    public Boolean getQuestionPublicById(@PathVariable Integer id) {
        Question question = questionService.getById(id);
        Integer ispublic = question.getIspublic();
        if (ispublic == 1) {
            return true;
        } else {
            return false;
        }
    }
}

