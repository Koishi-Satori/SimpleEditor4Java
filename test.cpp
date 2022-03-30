#include <bits/stdc++.h>

struct edge {
    int f,t,w;

    bool operate==(const edge &e) const {
        return this->f == e.f && this.t == e.t && this.w = e.w;
    }

    edge(int f, int t, int w) : f(f), t(t), w(w) {}
};

template<typename T>
struct node {
    T data;
    std::set<edge> edges;

    bool operate==(const node<T> &n) const {
        return this->data == n.data && this->edges == n.edges;
    }

    node(T data, std::set<edge> edges) : data(data), edges(std::move(edges)) {}

    public:
        void link(int f, int t, int w) {
            this->edges.insert(edge(f, t, w));
        }
};

struct Test {
    
};
 
template<typename T>
struct dfs_invoker {
    std::vector<node<T>> graph;
    bool *vis;

    dfs_invoker(std::vector<node<T>> graph, bool *vis) : graph(std::move(graph)), vis(vis) {};

    std::vector<T> dfs () {
        return dfsImpl(0);
    }

    std::vector<T> dfsImpl (int i) {
        std::vector<T> v;
        for (edge &e : graph[i].edges) {
            const int to = e->to;
            if (!vis[to]) {
                for (const T &t : dfsImpl(to)) {
                    v.push_back(t);
                }
            }
        }
        return v;
    }
}

std::vector<T> dfs (std::vector<node<T>> graph) {
    dfs_invoker<T> invoker(graph, bool[graph.size()]);
    return invoker.dfs();
}

template<typename T>
std::vector<node<T>> prim (std::vector<node<T>> graph) {
    std::vector<node<std::string>> tree;
    const int size = graph.size();
    int vis[size];
    std::vector<int> v,s;
    for (int i = 0; i < size; ++i) {
        s.push_back(i);
    }
    int pointer = 0;
    while (!s.empty()) {
        int min = 0x7fffffff, pwd = -1;
        vis[pointer] = true;
        for (edge &e : graph[pointer].edges) {
            if (vis[e.to]) {
                continue;
            }
            if (min > e.w) {
                min = e.w;
                pwd = e.to;
            }
        }
        if (pwd == -1) {
            for (int &i : t) {
                for (edge &e : graph[i].edges) {
                    if (vis[e.to]) {
                        continue;
                    }
                    if (min > e.w) {
                        min = e.w;
                        pwd = e.to;
                    }
                }
            }
        }
        tree.push_back(node<std::string>(graph[pointer].data, std::set<edge>()));
        tree.link(pointer, pwd, min);
        pointer = pwd;
        t.push_back(pointer);
        s.erase(pointer);
    }
    return tree;
}

//reload cout of std::vector<T>
template<typename T>
std::ostream &operator<<(std::ostream &s, const std::vector<T> &vs) const {
    const int size = vs.size() - 1;
    s << "[";
    for (int i = 0; i < size; ++i) {
        s << vs[i] << " ,";
    }
    s << vs[size]  << "]";
    return s;
}

int main () {
    std::vector<node<std::string>> graph;
    graph.push_back(node<std::string>("1", std::set<edge>()));
    graph.push_back(node<std::string>("2", std::set<edge>()));
    graph.push_back(node<std::string>("3", std::set<edge>()));
    graph.push_back(node<std::string>("4", std::set<edge>()));
    graph.push_back(node<std::string>("5", std::set<edge>()));
    graph.push_back(node<std::string>("6", std::set<edge>()));
    graph.link(0, 1, 6);
    graph.link(0, 2, 1);
    graph.link(0, 3, 5);
    graph.link(1, 2, 5);
    graph.link(1, 4, 3);
    graph.link(4, 2, 6);
    graph.link(4, 5, 6);
    graph.link(5, 2, 4);
    graph.link(5, 3, 2);
    graph.link(3, 0, 5);
    graph.link(3, 2, 5);
    std::cout << ::dfs(::prim(graph)) << endl;
    return 0;
}
