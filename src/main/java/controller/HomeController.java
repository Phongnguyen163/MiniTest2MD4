package controller;

import model.Post;
import service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Controller
@RequestMapping("")
public class HomeController {
    @Autowired
    IPostService IPostService;

    @GetMapping
    public ModelAndView showHome(@PageableDefault(value = 4) Pageable pageable) {
        Page<Post> posts = IPostService.findAll(pageable);
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }

    @GetMapping("/create")
    public ModelAndView showCreate() {
        ModelAndView modelAndView = new ModelAndView("/post/create");
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView create(Post post) {
        post.setCreateAt(LocalDateTime.now());
        post.setLikes(0L);
        IPostService.save(post);
        ModelAndView modelAndView = new ModelAndView("redirect:/");
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView showEdit(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("/post/edit");
        modelAndView.addObject("post", IPostService.findById(id).get());
        return modelAndView;
    }

    @PostMapping("/edit/{id}")
    public ModelAndView edit(Post post, @PathVariable Long id) {
        Post oldPost = IPostService.findById(id).get();
        post.setLikes(oldPost.getLikes());
        post.setCreateAt(oldPost.getCreateAt());
        IPostService.save(post);
        ModelAndView modelAndView = new ModelAndView("redirect:/");
        return modelAndView;
    }

    @GetMapping("/search")
    public ModelAndView search(@RequestParam String title, @RequestParam String dateFrom, @RequestParam String dateTo, @PageableDefault(value = 4) Pageable pageable) {
        if ((dateFrom.equals("") && dateTo.equals(""))) {
            dateFrom = "1900-01-01T00:00:00";
            dateTo = String.valueOf(LocalDateTime.now());
        }
        Iterable<Post> posts = IPostService.findByTitle('%' + title + '%', LocalDateTime.parse(dateFrom), LocalDateTime.parse(dateTo), pageable);
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable Long id) {
        IPostService.remove(id);
        ModelAndView modelAndView = new ModelAndView("redirect:/");
        return modelAndView;
    }

    @GetMapping("/like/{id}")
    public ModelAndView like(@PathVariable Long id) {
        Post post = IPostService.findById(id).get();
        post.setLikes(post.getLikes() + 1);
        IPostService.save(post);
        ModelAndView modelAndView = new ModelAndView("redirect:/");
        return modelAndView;
    }

    @GetMapping("/orderbylikes")
    public ModelAndView orderByLikes() {
        Iterable<Post> posts = IPostService.findAllByOrOrderByLikes();
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }

    @GetMapping("/findpostnewest")
    public ModelAndView findTop4PostNewest() {
        Iterable<Post> posts = IPostService.findTopByCreateAt();
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }
}