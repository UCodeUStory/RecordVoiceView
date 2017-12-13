

## 模仿 小米 录音界面



### 分析特点

   - 根据声音分贝可以自由控制高度
   
   - 曲线是动态，像是沿着一个方向在行走
   
   - 两个端点位置不变曲线两端由低到高
   
### 实现方式
  - 1 这里通过正弦函数 f(x) = sin(x)算法来实现，也可以用贝塞尔曲线，不过处理细节部分往往不太好
 
  - 2 f(x) = sin(x) x 是弧度 0 到2π 区间周期性变化，而我们的屏幕是宽度,0 到 480+ 以上的像素，怎么样转换成弧度呢?
   我们知道 角度可以转化成弧度，而我的们的值恰好很接近角度周期性变化，因此，把每个像素看成一个角度，然后对应转化成弧度
   0 到 360 对应 0 到 2π，Math有转化函数
  
  
     private float calculateY(float x) {
  
          /**
           * 弧度取值范围 0 2π
           */
          double rad = Math.toRadians(x);
  
          double fx = Math.sin(rad);
  
          float dy = (float) (fx * 50);
  
          return (float) (mCenterY - dy);
  
      }
 
  - 3 通过上面计算方法，增加点的数量可以得到一条平滑的曲线，点的数量可以按实际像素宽度计算，如果线的宽度大于1px，可以这样计算
    点的数量 >= 屏幕宽度/线的宽度,这样效果最好
    
  - 4 通过上面方法我们只能得到一条静态的曲线，当然我们是希望他动起来，不能乱动，每个点要按sinX 成周期性变化
     因此，可以通过时间控制 加入offset参数，offset 随着时间慢慢增加，让后将这个参数加入到sin函数里面
     
  
     private float calculateY(float x, float offset) {
  
          /**
           * 弧度取值范围 0 2π
           */
          double rad = Math.toRadians(x);
  
          double fx = Math.sin(rad + offset);
  
          float dy = (float) (fx * 50);
  
          return (float) (mCenterY - dy);
  
      }
  
  - 5 得到动态的曲线后,和我们预期还是不太一样，两端没有固定
     通过判断两端点，会得到两个端点变化明显，并且我们的目的是两端收敛
     
  - 6 模仿小米还需要Path LineTo
  
  - 绘制3条相交的部分
  
    




